package com.elkdocs.notestudio.presentation.page_viewer_screen

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
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
import com.elkdocs.notestudio.R
import com.elkdocs.notestudio.databinding.FragmentPageViewerBinding
import com.elkdocs.notestudio.domain.model.MyPageModel
import com.elkdocs.notestudio.util.Constant.BLUE_LINE_COLOR
import com.elkdocs.notestudio.util.Constant.PAGE_COLOR_LIGHT_BEIGE
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections

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
        adapter = PageViewerAdapter(
            onPageClick = { pageDetail ->
                findNavController().navigate(
                    PageViewerFragmentDirections.actionPageViewerFragmentToPageEditFragment(pageDetail.pageId!!)
                )
            },
            onDeleteClick = { _ ->
                //showDeleteConfirmationDialog(qrData)
            },
            onPageLongClick = { _ ->
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
            requireActivity().runOnUiThread{
                adapter.clearSelectedItems()
                setSelectModeEnabled(false)
            }

//            setSelectModeEnabled(false)
//            adapter.clearSelectedItems()
//            adapter.notifyDataSetChanged()
        }

        binding.pdfIcon.setOnClickListener {
            findNavController().navigate(PageViewerFragmentDirections.actionPageViewerFragmentToExportDocumentFragment(navArgs.folderId,navArgs.folderName))
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

//    private fun createPdfFromAllPages() {
//        lifecycleScope.launch {
//            val bitmapList = viewModel.allPages.value.map {
//                it.bitmap
//            }
//            PdfUtility.createPdf(requireContext(),bitmapList,navArgs.folderName){
//                 PdfUtility.openPdfFile(requireContext(),it)
//            }
//        }
//    }


    private fun showDeleteAllDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete selected items?")
            .setPositiveButton("Delete") { dialog, which ->
                val selectedList = adapter.selectedItems.toList()
                val isDeleteAll = selectedList.size == adapter.itemCount

                lifecycleScope.launch(Dispatchers.IO) {
                    if (selectedList.isNotEmpty()) {
                        if (isDeleteAll) {
                            viewModel.deleteFolder(navArgs.folderId)
                            withContext(Dispatchers.Main) {
                                findNavController().navigateUp()
                            }

                        } else {
                            selectedList.forEach { page ->
                                viewModel.deleteAll(page)
                            }
                            viewModel.onEvent(PageViewerEvent.DecreasePageCount(navArgs.folderId, selectedList.size))
                        }
                    }
                }
                adapter.clearSelectedItems()
                setSelectModeEnabled(false)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }



    private fun addingInitialPageForFirstTime() {

        val pageBitmap = ContextCompat.getDrawable(requireContext(), R.drawable.page_image)
            ?.toBitmap(1024,1833,Bitmap.Config.ARGB_8888)
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
                        language = "English",
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
                        headingUnderline = false,
                        isLayoutFlipped = false
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
                    if(it.isNotEmpty()){
                        it.first().folderId?.let {folderId ->
                            val folderIcon = it.first().bitmap
                            viewModel.onEvent(PageViewerEvent.UpdateFolderIcon(folderId,folderIcon))
                        }
                    }


                    adapter.clearSelectedItems()
                }
            }
        }
    }

    private fun setClickListeners() {

        val pageBitmap = ContextCompat.getDrawable(requireContext(), R.drawable.page_image)
            ?.toBitmap(1024,1833,Bitmap.Config.ARGB_8888)
        binding.fabImagePicker.setOnClickListener {


            val page = MyPageModel(
                folderId = navArgs.folderId,
                uriIndex = 0,
                notesText = "",
                headingText = "",
                textAlignment = 0,
                fontSize = 20f,
                language = "English",
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
                headingUnderline = false,
                isLayoutFlipped = false
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