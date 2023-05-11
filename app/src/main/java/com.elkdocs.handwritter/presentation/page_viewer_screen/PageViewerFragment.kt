package com.elkdocs.handwritter.presentation.page_viewer_screen

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.FragmentPageViewerBinding
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.util.Constant
import com.elkdocs.handwritter.util.Constant.BLUE_LINE_COLOR
import com.elkdocs.handwritter.util.Constant.LINE_COLOR_BLUE
import com.elkdocs.handwritter.util.Constant.PAGE_COLOR_LIGHT_BEIGE
import com.elkdocs.handwritter.util.OtherUtility
import com.elkdocs.handwritter.util.OtherUtility.drawableToBitmap
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class PageViewerFragment : Fragment() {

    private lateinit var binding : FragmentPageViewerBinding
    private val viewModel: PageViewerViewModel by viewModels()
    private lateinit var adapter : PageViewerAdapter

    private val navArgs: PageViewerFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPageViewerBinding.inflate(layoutInflater)

//        adapter = PageViewerAdapter({
//
//            findNavController().navigate(PageViewerFragmentDirections.actionPageViewerFragmentToPageEditFragment(pageDetail))
//        })

        adapter = PageViewerAdapter(
            onPageClick = { pageDetail ->
                //handle item click click on item
                findNavController().navigate(PageViewerFragmentDirections.actionPageViewerFragmentToPageEditFragment(pageDetail))
            },
            onDeleteClick = { qrData ->
                Toast.makeText(requireContext(),"onDeleteCalled",Toast.LENGTH_SHORT).show()
                //showDeleteConfirmationDialog(qrData)
            },
            onPageLongClick = { qrData ->
                if (!adapter.isSelectModeEnabled){
                    adapter.setIsSelectedModeEnabled(true)
                    binding.deleteIcon.visibility = View.VISIBLE
                    binding.pdfIcon.visibility = View.INVISIBLE
                    adapter.notifyDataSetChanged()
                }

            },
        )


        viewModel.updateFolderId(navArgs.folderId)
        binding.rvPages.adapter = adapter
        binding.rvPages.layoutManager = GridLayoutManager(requireContext(),3)

        setClickListeners()
        setObserver()
        addingInitialPageForFirstTime()
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.pdfIcon.setOnClickListener {

            lifecycleScope.launch {
                        val bitmap = viewModel.allPages.value.map {
                            saveImageToInternalStorage(it.bitmap, it.pageId.toString())
                            it.bitmap
                        }
                        createPdf(bitmap)
                    }
              }

        binding.deleteIcon.setOnClickListener {
            if (adapter.selectedItems.isEmpty()) {
                adapter.setIsSelectedModeEnabled(true)

                adapter.notifyDataSetChanged()
            } else {
                showDeleteAllDailog()

            }
        }

        return binding.root
    }

    private fun selectAllItems() {
        adapter.toggleSelectAll()
        adapter.notifyDataSetChanged()
    }

    private fun addingInitialPageForFirstTime() {

        val pageBitmap = drawableToBitmap(
            ContextCompat.getDrawable(requireContext(), R.drawable.page_image)
        )
        lifecycleScope.launch {

            viewModel.allPages2.collectLatest {
                if (it.isEmpty()) {
                    val page = MyPageModel(
                        folderId = navArgs.folderId,
                        uriIndex = 0,
                        notesText = "",
                        fontSize = 20f,
                        fontStyle = R.font.caveat_variablefont_wght,
                        fontType = Typeface.NORMAL,
                        letterSpace = 0f,
                        textAndLineSpace = 0.105f,
                        addLines = true,
                        lineColor = BLUE_LINE_COLOR,
                        pageColor = PAGE_COLOR_LIGHT_BEIGE,
                        bitmap = pageBitmap!!
                    )
                    viewModel.onEvent(PageViewerEvent.AddPage(page))
                }
            }
        }
    }

    private fun setObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allPages.collectLatest {
                    adapter.setAllPages(it)
                }
            }
        }
    }

    private fun setClickListeners() {
        val pageBitmap = drawableToBitmap(ContextCompat.getDrawable(requireContext(),R.drawable.page_image))
        binding.fabImagePicker.setOnClickListener {
                val page = MyPageModel(
                    folderId = navArgs.folderId,
                    uriIndex = 0,
                    notesText = "",
                    fontSize = 20f,
                    fontStyle = R.font.caveat_variablefont_wght,
                    fontType = Typeface.NORMAL,
                    letterSpace = 0f,
                    textAndLineSpace = 0.105f,
                    addLines = true,
                    lineColor = BLUE_LINE_COLOR,
                    pageColor = PAGE_COLOR_LIGHT_BEIGE,
                    bitmap = pageBitmap!!
                )
                viewModel.onEvent(PageViewerEvent.AddPage(page))
        }
    }


    private fun createPdf(bitmaps: List<Bitmap>) {
        val pdfFile = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() +
                    File.separator + "HandWriter_pdf" + System.currentTimeMillis() / 1000 + ".pdf"
        )

        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        // Create a new document and a PDF writer
        val document = Document()
        val writer = PdfWriter.getInstance(document, FileOutputStream(pdfFile))

        executor.execute {
            //Background work here

            // Open the document
            document.open()

            // Loop through the bitmaps and add each image to the PDF document
            for (bitmap in bitmaps) {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                val imageBytes: ByteArray = stream.toByteArray()
                val image = com.itextpdf.text.Image.getInstance(imageBytes)

                // Add the image to the document
                document.pageSize = image
                document.newPage()
                image.setAbsolutePosition(0f, 0f)
                document.add(image)
            }

            // Close the document and the PDF writer
            document.close()
            writer.close()
            val intent = Intent(Intent.ACTION_VIEW)
            val uri =
                FileProvider.getUriForFile(requireContext(), "com.elkdocs.handwriter.fileprovider", pdfFile)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }
        handler.post(Runnable {
        })
    }
    private fun saveImageToInternalStorage(bitmap: Bitmap, imageCount: String): Uri {
        //creating file that is only accessible with this app , other app and user cant interact with it
        val file = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() +
                    File.separator + "HandWriter_Pro_$imageCount" + System.currentTimeMillis() / 1000 + ".png"
        )
        try {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            val fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //Log.e("myTag ss", Uri.parse((file.absolutePath)).toString())
        val uri = FileProvider.getUriForFile(requireActivity(), "com.elkdocs.handwriter.fileprovider", file)
        return uri
    }


    private fun showDeleteAllDailog() {
        val dialogDeleteAll = MaterialAlertDialogBuilder(requireContext()).apply {
            setMessage("Are you sure you want to delete selected items?")
            setPositiveButton("Delete") { dialog, which ->
                adapter.selectedItems.let {
                    if (it.isNotEmpty()) {
                        it.forEach { page ->
                            viewModel.onEvent(PageViewerEvent.DeletePage(page))
                        }
                    }
                    adapter.clearSelectedItems()
                    adapter.setIsSelectedModeEnabled(false)
                    binding.pdfIcon.visibility = View.VISIBLE
                }
            }
            setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
                // handle the cancel
            }
        }
        val dialog = dialogDeleteAll.create()
        dialog.show()
    }



}