package com.elkdocs.handwritter.presentation.folder_screen

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.CustomPopupMenuBinding
import com.elkdocs.handwritter.databinding.FragmentMainBinding
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.presentation.MainActivity
import com.elkdocs.handwritter.util.Constant.IS_LINEAR
import com.elkdocs.handwritter.util.PdfUtility.createPdf
import com.elkdocs.handwritter.util.PdfUtility.downloadPdfToGallery
import com.elkdocs.handwritter.util.PdfUtility.openPdfFile
import com.elkdocs.handwritter.util.PdfUtility.sharePdf
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : Fragment(),MenuProvider {
    
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: FolderAdapter
    private lateinit var toggle : ActionBarDrawerToggle
    private val viewModel: FolderViewModel by viewModels()
    private var menu : Menu? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater)
        val menuHost : MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)

        toggle = ActionBarDrawerToggle(requireActivity(),binding.drawerLayout,R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
        val isLinear = sharedPreferences.getBoolean(IS_LINEAR, true)
        binding.menuIcon.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }

        adapter = FolderAdapter(
            onFolderClick = { folderId , folderName ->
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToPageViewerFragment(folderId,folderName))
            }
            , onFolderLongClick = {
                if (!adapter.isSelectModeEnabled) {
                    setSelectModeEnabled(true)
                }
            },
            onMoreOptionClick = { folderId,folderName, itemImageView ->
                popupMenu(folderId,folderName,itemImageView)
            },
            isLinear = isLinear
        )

         binding.rvMyFolderListView.adapter = adapter
         setViewType(isLinear)

        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item1 -> { Toast.makeText(requireContext(),"1",Toast.LENGTH_SHORT).show() }
                R.id.item2 -> { Toast.makeText(requireContext(),"2",Toast.LENGTH_SHORT).show() }
                R.id.item3 -> { Toast.makeText(requireContext(),"3",Toast.LENGTH_SHORT).show() }
            }
            true
        }
        binding.addFolderImageView.setOnClickListener {addFolderAndNavigate()}
        binding.fabMain.setOnClickListener {addFolderAndNavigate()}
        
        setObservers()


        binding.gridImageView.setOnClickListener {
            val newIsLinear = !sharedPreferences.getBoolean(IS_LINEAR, false)
            setViewType(newIsLinear)
            adapter = FolderAdapter(
                onFolderClick = { folderId , folderName ->
                    findNavController().navigate(MainFragmentDirections.actionMainFragmentToPageViewerFragment(folderId,folderName))
                },
                onFolderLongClick = {
                    if (!adapter.isSelectModeEnabled) {
                        setSelectModeEnabled(true)
                    }
                },
                onMoreOptionClick = { folderId,folderName, itemImageView ->
                    //onShareClick(it)
                    popupMenu(folderId,folderName,itemImageView)

                },
                isLinear = newIsLinear
            )
            binding.rvMyFolderListView.adapter = adapter
            setObservers()
        }
        setClickListeners()


        
        return binding.root
    }

//    private fun popupMenu(id: Long, itemImageView: ImageView) {
//        val items = arrayOf("Rename", "Share", "PDF", "Download")
//
//        AlertDialog.Builder(requireContext())
//            .setItems(items) { _, which ->
//                when (which) {
//                    0 -> {
//                        Toast.makeText(requireContext(), "Rename", Toast.LENGTH_SHORT).show()
//                    }
//                    1 -> {
//                        onShareClick(id)
//                    }
//                    2 -> {
//                        Toast.makeText(requireContext(), "PDF", Toast.LENGTH_SHORT).show()
//                    }
//                    3 -> {
//                        Toast.makeText(requireContext(), "Download", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//            .show()
//    }
//

//    private fun popupMenu(id : Long,itemImageView: ImageView){
//        val popupMenu = PopupMenu(requireContext(),itemImageView)
//        popupMenu.inflate(R.menu.popup_menu)
//        popupMenu.setOnMenuItemClickListener {
//            when(it.itemId){
//                R.id.item_rename -> {
//                    Toast.makeText(requireContext(),"Rename",Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.item_share -> {
//                    onShareClick(id)
//                    true
//                }
//                R.id.item_pdf -> {
//                    Toast.makeText(requireContext(),"Pdf",Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.item_download -> {
//                    Toast.makeText(requireContext(),"download",Toast.LENGTH_SHORT).show()
//                    true
//                }
//
//                else -> {true}
//            }
//        }
//        itemImageView.setOnClickListener{
//            try{
//                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
//                popup.isAccessible = true
//                val menu = popup.get(popupMenu)
//                menu.javaClass
//                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
//                    .invoke(menu,true)
//            }catch(e : Exception){
//                e.printStackTrace()
//            } finally {
//                popupMenu.show()
//            }
//
//
//        }
//    }

//    private fun popupMenu(id: Long, itemImageView: ImageView) {
//        val dialog = Dialog(requireContext())
//        dialog.setContentView(R.layout.custom_popup_menu)
//
//        val itemRename = dialog.findViewById<CardView>(R.id.item_rename)
//        val itemShare = dialog.findViewById<CardView>(R.id.item_share)
//        val itemPdf = dialog.findViewById<CardView>(R.id.item_pdf)
//        val itemDownload = dialog.findViewById<CardView>(R.id.item_download)
//
//        itemRename.setOnClickListener {
//            Toast.makeText(requireContext(), "Rename", Toast.LENGTH_SHORT).show()
//            dialog.dismiss()
//        }
//
//        itemShare.setOnClickListener {
//            onShareClick(id)
//            dialog.dismiss()
//        }
//
//        itemPdf.setOnClickListener {
//            Toast.makeText(requireContext(), "Pdf", Toast.LENGTH_SHORT).show()
//            dialog.dismiss()
//        }
//
//        itemDownload.setOnClickListener {
//            Toast.makeText(requireContext(), "Download", Toast.LENGTH_SHORT).show()
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }
private fun popupMenu(id: Long, folderName : String ,itemImageView: ImageView) {
    val bottomSheetDialog = BottomSheetDialog(requireContext())
    val dialogBinding = CustomPopupMenuBinding.inflate(layoutInflater)
    bottomSheetDialog.setContentView(dialogBinding.root)
    dialogBinding.folderName.text = folderName

     dialogBinding.itemDownload.setOnClickListener {
        getPdfFile(id,folderName) { pdfFile ->
            lifecycleScope.launch(Dispatchers.IO) {
                val isSuccessful = downloadPdfToGallery(requireContext(), pdfFile)

                withContext(Dispatchers.Main) {
                    if (isSuccessful) {
                        Toast.makeText(requireContext(), "PDF saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to save PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        bottomSheetDialog.dismiss()
    }

    dialogBinding.itemShare.setOnClickListener {
        getPdfFile(id,folderName){
            sharePdf(requireContext(),it)
        }
        bottomSheetDialog.dismiss()
    }

    dialogBinding.itemPdf.setOnClickListener {
        getPdfFile(id,folderName){
            openPdfFile(requireContext(),it)
        }
        bottomSheetDialog.dismiss()
    }

    dialogBinding.itemDelete.setOnClickListener {
         showDeleteDialog(id)
        bottomSheetDialog.dismiss()
    }



    bottomSheetDialog.show()
}
    private fun getPdfFile(folderId : Long, folderName : String,pdfFile : (file : File) -> Unit, ){
        lifecycleScope.launch {
            val pages = viewModel.getAllPagesById(folderId)
            val pageModels = pages.first() // Get the initial value of the pages list
            if (pageModels.isNotEmpty()) {
                val bitmapList = pageModels.map { it.bitmap }
                createPdf(requireContext(), bitmapList , folderName){
                   pdfFile(it)
                }
            } else {
                Toast.makeText(requireContext(), "Empty", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun setClickListeners(){

        binding.folderDeleteImageView.setOnClickListener {
            if (adapter.selectedItems.isEmpty()) {
                setSelectModeEnabled(true)
                Snackbar.make(requireView(),"Please select item to delete", Snackbar.LENGTH_SHORT).show()
            } else {
                showDeleteAllDialog()
            }
        }

        binding.checkFolderImageView.setOnClickListener {
            setSelectModeEnabled(true)
        }

        binding.selectAllImageView.setOnClickListener {
            adapter.toggleSelectAll()
            adapter.notifyDataSetChanged()
        }

        binding.closeImageView.setOnClickListener {
            setSelectModeEnabled(false)
            adapter.clearSelectedItems()
        }

        binding



    }
    private fun showDeleteAllDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete selected items?")
            .setPositiveButton("Delete") { dialog, which ->
                adapter.selectedItems.let { it ->
                    if (it.isNotEmpty()) {
                        it.forEach { folder ->
                            folder.folderId?.let { id ->
                                viewModel.onEvent(FolderEvent.DeleteFolderWithPages(id)){ folderId,folderName ->

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

    private fun showDeleteDialog(folderId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete the folder")
            .setPositiveButton("Delete") { dialog, which ->
                viewModel.onEvent(FolderEvent.DeleteFolderWithPages(folderId)) { _, _ ->
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    private fun setViewType(isLinear : Boolean){
        sharedPreferences.edit().putBoolean(IS_LINEAR, isLinear).apply()
        if (isLinear) {
            binding.rvMyFolderListView.layoutManager = LinearLayoutManager(requireContext())
        } else {
            binding.rvMyFolderListView.layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }


    private fun addFolderAndNavigate(){
        val folder = MyFolderModel(
            folderName = "",
            folderIcon = "",
            pageCount = 0,
            lastUpdated = System.currentTimeMillis()
        )
        viewModel.onEvent(FolderEvent.AddFolder(folder)){ folderId,folderName ->
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToPageViewerFragment(folderId,folderName))

        }
    }


    
    private fun setObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allFolders.collect { foldersList->
                    binding.noDocuments.isVisible = foldersList.isEmpty()
                    adapter.setAllFolder(foldersList.reversed())
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
     menuInflater.inflate(R.menu.nav_drawer_menu,menu)
        this.menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(menuItem)){
            return true
        }
        return true
    }

    private fun setSelectModeEnabled(isEnabled: Boolean) {
        adapter.setIsSelectedModeEnabled(isEnabled)
        binding.checkFolderImageView.isVisible = !isEnabled
        binding.gridImageView.isVisible = !isEnabled
        binding.addFolderImageView.isVisible = !isEnabled
        binding.allDocsHeadingTextView.isVisible =!isEnabled
        binding.fabMain.isVisible = !isEnabled
        binding.folderDeleteImageView.isVisible = isEnabled
        binding.selectAllImageView.isVisible = isEnabled
        binding.closeImageView.isVisible = isEnabled
        adapter.notifyDataSetChanged()
    }

}