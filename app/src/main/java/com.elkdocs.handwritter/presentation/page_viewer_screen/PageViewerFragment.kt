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
import com.elkdocs.handwritter.util.PdfUtility
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
                    PageViewerFragmentDirections.actionPageViewerFragmentToPageEditFragment(pageDetail.pageId!!)
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
                Toast.makeText(requireContext(),"${adapter.selectedItems.size}",Toast.LENGTH_SHORT).show()
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
            val bitmapList = viewModel.allPages.value.map {
                it.bitmap
            }
            PdfUtility.createPdf(requireContext(),bitmapList,navArgs.folderName){
                 PdfUtility.openPdfFile(requireContext(),it)
            }
        }
    }

    private fun showDeleteAllDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete selected items?")
            .setPositiveButton("Delete") { dialog, which ->
                adapter.selectedItems.let { selectedList ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (selectedList.isNotEmpty()) {
                            if (selectedList.size == adapter.itemCount) {
//                                viewModel.onEvent(PageViewerEvent.DeleteFolder(navArgs.folderId))
                                viewModel.deleteFolder(navArgs.folderId)
                                adapter.clearSelectedItems()
                                withContext(Dispatchers.Main) {
                                    findNavController().navigateUp()
                                }
                            } else {
                                Log.v("Tag","${selectedList.size}")
                                val list = selectedList.toList()
                                list.forEach { page ->
                                    viewModel.onEvent(PageViewerEvent.DeletePage(page,selectedList.size))

                                }



                            }
                        }
                    }
                    viewModel.onEvent(PageViewerEvent.DecreasePageCount(navArgs.folderId,selectedList.size))

                    setSelectModeEnabled(false)
                }
                adapter.clearSelectedItems()

            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    private fun addingInitialPageForFirstTime() {

        val pageBitmap = drawableToBitmap(
            ContextCompat.getDrawable(requireContext(), R.drawable.page_image)
        )
        lifecycleScope.launch {

            viewModel.allPages2.collectLatest { it ->
                if (it.isEmpty()) {
                    val page = MyPageModel(
                        folderId = navArgs.folderId,
                        uriIndex = 0,
                        notesText = "",
                        headingText = "",
                        textAlignment = 0,
                        fontSize = 20f,
                        fontStyle = R.font.caveat_variablefont_wght,
                        fontType = Typeface.NORMAL,
                        headingFontType = Typeface.NORMAL,
                        letterSpace = 0f,
                        textAndLineSpace = 10f,
                        addLines = true,
                        lineColor = BLUE_LINE_COLOR,
                        inkColor = Color.BLACK,
                        pageColor = PAGE_COLOR_LIGHT_BEIGE,
                        bitmap = pageBitmap!!,
                        underline = false,
                        headingUnderline = false
                    )
                    viewModel.onEvent(PageViewerEvent.AddPage(page))
                    page.folderId?.let {folderId ->
                        viewModel.onEvent(PageViewerEvent.IncreasePageCount(folderId))
                    }

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
                headingText = "",
                textAlignment = 0,
                fontSize = 20f,
                fontStyle = R.font.caveat_variablefont_wght,
                fontType = Typeface.NORMAL,
                headingFontType = Typeface.NORMAL,
                letterSpace = 0f,
                textAndLineSpace = 0.105f,
                addLines = true,
                lineColor = BLUE_LINE_COLOR,
                inkColor = Color.BLACK,
                pageColor = PAGE_COLOR_LIGHT_BEIGE,
                bitmap = pageBitmap!!,
                underline = false,
                headingUnderline = false
            )
            viewModel.onEvent(PageViewerEvent.AddPage(page))
            page.folderId?.let {
                viewModel.onEvent(PageViewerEvent.IncreasePageCount(it))
            }
        }
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