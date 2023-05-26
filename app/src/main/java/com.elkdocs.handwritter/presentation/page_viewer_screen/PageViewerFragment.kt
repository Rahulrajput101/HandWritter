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
import android.util.Log
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
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
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Collections
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
        // Inflate the layout for this fragment
        binding = FragmentPageViewerBinding.inflate(layoutInflater)

        adapter = PageViewerAdapter(
            onPageClick = { pageDetail ->
                findNavController().navigate(
                    PageViewerFragmentDirections.actionPageViewerFragmentToPageEditFragment(pageDetail)
                )
            },
            onDeleteClick = { qrData ->
                //showDeleteConfirmationDialog(qrData)
            },
            onPageLongClick = { qrData ->
                if (!adapter.isSelectModeEnabled) {
                    setSelectModeEnabled(true)
                }
            }
        )

        viewModel.updateFolderId(navArgs.folderId)
        binding.rvPages.adapter = adapter
        binding.rvPages.layoutManager = GridLayoutManager(requireContext(), 3)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper
        .SimpleCallback(
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT  or ItemTouchHelper.UP or ItemTouchHelper.DOWN
            ,0){
            override fun onMove(
                recyclerView: RecyclerView,
                source : RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val sourcePosition = source.adapterPosition
                val targetPosition = target.adapterPosition
                val list = viewModel.allPages.value

                Collections.swap(list,sourcePosition,targetPosition)
                adapter.notifyItemMoved(sourcePosition,targetPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

        })

        itemTouchHelper.attachToRecyclerView(binding.rvPages)

        setClickListeners()
        setIconClickListeners()
        setObserver()
        addingInitialPageForFirstTime()

        return binding.root
    }

    private fun setIconClickListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.closeButton.setOnClickListener {
            setSelectModeEnabled(false)
            adapter.clearSelectedItems()
        }

        binding.pdfIcon.setOnClickListener {
            createPdfFromAllPages()
        }

        binding.deleteIcon.setOnClickListener {
            if (adapter.selectedItems.isEmpty()) {
                setSelectModeEnabled(true)
                Snackbar.make(requireView(),"Please select item to delete",Snackbar.LENGTH_SHORT).show()
            } else {
                showDeleteAllDialog()
            }
        }

        binding.selectAll.setOnClickListener {
            adapter.toggleSelectAll()
            adapter.notifyDataSetChanged()
        }
    }

    private fun createPdfFromAllPages() {
        lifecycleScope.launch {
            val bitmapList = viewModel.allPages.value.map { page ->
                saveImageToInternalStorage(page.bitmap, page.pageId.toString())
                page.bitmap
            }
            createPdf(bitmapList)
        }
    }

    private fun showDeleteAllDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete selected items?")
            .setPositiveButton("Delete") { dialog, which ->
                adapter.selectedItems.let {
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (it.isNotEmpty()) {
                            if (it.size == adapter.itemCount) {
//                                viewModel.onEvent(PageViewerEvent.DeleteFolder(navArgs.folderId))
                                viewModel.deleteFolder(navArgs.folderId)
                                adapter.clearSelectedItems()
                                withContext(Dispatchers.Main) {
                                    findNavController().navigateUp()
                                }
                            } else {
                                it.forEach { page ->
                                    viewModel.onEvent(PageViewerEvent.DeletePage(page))
                                }

                            }

                        }
                    }

                    setSelectModeEnabled(false)
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


//    private fun showDeleteAllDialog() {
//        MaterialAlertDialogBuilder(requireContext())
//            .setMessage("Are you sure you want to delete selected items?")
//            .setPositiveButton("Delete") { dialog, which ->
//                adapter.selectedItems.let { selectedItems ->
//                        if (selectedItems.isNotEmpty()) {
//                            if (selectedItems.size == adapter.itemCount) {
////                                val folderId = selectedItems[0].folderId
//                                viewModel.onEvent(PageViewerEvent.DeleteFolder(navArgs.folderId))
//
//                            } else {
//                                selectedItems.forEach { page ->
//                                    viewModel.onEvent(PageViewerEvent.DeletePage(page))
//                                }
//                            }
//
//
//                        }
//
//                    setSelectModeEnabled(false)
//                }
//            }
//            .setNegativeButton("Cancel") { dialog, which ->
//                dialog.dismiss()
//            }
//            .create()
//            .show()
//    }

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
                        textAlignment = 0,
                        fontSize = 20f,
                        fontStyle = R.font.caveat_variablefont_wght,
                        fontType = Typeface.NORMAL,
                        letterSpace = 0f,
                        textAndLineSpace = 10f,
                        addLines = true,
                        lineColor = BLUE_LINE_COLOR,
                        inkColor = Color.BLACK,
                        pageColor = PAGE_COLOR_LIGHT_BEIGE,
                        bitmap = pageBitmap!!,
                        underline = false
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
                textAlignment = 0,
                fontSize = 20f,
                fontStyle = R.font.caveat_variablefont_wght,
                fontType = Typeface.NORMAL,
                letterSpace = 0f,
                textAndLineSpace = 0.105f,
                addLines = true,
                lineColor = BLUE_LINE_COLOR,
                inkColor = Color.BLACK,
                pageColor = PAGE_COLOR_LIGHT_BEIGE,
                bitmap = pageBitmap!!,
                underline = false
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

    private fun setSelectModeEnabled(isEnabled: Boolean) {
        adapter.setIsSelectedModeEnabled(isEnabled)
        binding.pdfIcon.isVisible = !isEnabled
        binding.selectAll.isVisible = isEnabled
        binding.backButton.isVisible = !isEnabled
        binding.fabImagePicker.isVisible = !isEnabled
        binding.closeButton.isVisible = isEnabled
        adapter.notifyDataSetChanged()
    }
}