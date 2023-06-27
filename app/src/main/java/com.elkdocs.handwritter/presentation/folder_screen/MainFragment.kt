package com.elkdocs.handwritter.presentation.folder_screen

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
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
import com.elkdocs.handwritter.databinding.DialogInkColorBinding
import com.elkdocs.handwritter.databinding.DialogRenameFolderBinding
import com.elkdocs.handwritter.databinding.FragmentMainBinding
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.presentation.MainActivity
import com.elkdocs.handwritter.presentation.page_edit_screen.InkColorAdapter
import com.elkdocs.handwritter.presentation.page_edit_screen.PageEditEvent
import com.elkdocs.handwritter.util.Constant.APP_THEME_PREF
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class MainFragment : Fragment(),MenuProvider {
    
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: FolderAdapter
    private lateinit var toggle : ActionBarDrawerToggle
    private val viewModel: FolderViewModel by viewModels()
    private var menu : Menu? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    @Named("theme")
     lateinit var appThemePref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater)
        setIconColorByTheme()
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
            onMoreOptionClick = { folderId,folderName, date ,itemImageView ->
                popupMenu(folderId,folderName,date,itemImageView)
            },
            isLinear = isLinear
        )

         binding.rvMyFolderListView.adapter = adapter
         setViewType(isLinear)

        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item1 -> {
                    findNavController().navigate(MainFragmentDirections.actionMainFragmentToAboutFragment())
                }
                R.id.item2 -> { Toast.makeText(requireContext(),"2",Toast.LENGTH_SHORT).show() }
                R.id.item3 -> { Toast.makeText(requireContext(),"3",Toast.LENGTH_SHORT).show() }
                R.id.item4 -> {
                    themeDialog()
                }
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
                onMoreOptionClick = { folderId,folderName, date,itemImageView ->
                    //onShareClick(it)
                    popupMenu(folderId,folderName,date,itemImageView)

                },
                isLinear = newIsLinear
            )
            binding.rvMyFolderListView.adapter = adapter
            setObservers()
        }
        setClickListeners()
        handleRenameEvent()

        
        return binding.root
    }


    private fun popupMenu(id: Long, folderName: String, date : String,itemImageView: ImageView) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val dialogBinding = CustomPopupMenuBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.folderName.text = folderName
        dialogBinding.folderDateMenu.text = date

        dialogBinding.itemDownload.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToExportDocumentFragment(id,folderName))
//            getPdfFile(id, folderName) { pdfFile ->
//                lifecycleScope.launch(Dispatchers.IO) {
//                    val isSuccessful = downloadPdfToGallery(requireContext(), pdfFile)
//
//                    withContext(Dispatchers.Main) {
//                        if (isSuccessful) {
//                            Toast.makeText(requireContext(), "PDF downloaded ", Toast.LENGTH_SHORT)
//                                .show()
//                        } else {
//                            Toast.makeText(requireContext(), "Failed to PDF", Toast.LENGTH_SHORT)
//                                .show()
//                        }
//                    }
//                }
//            }
            bottomSheetDialog.dismiss()
        }

        dialogBinding.itemShare.setOnClickListener {
            getPdfFile(id, folderName) {
                sharePdf(requireContext(), it)
            }
            bottomSheetDialog.dismiss()
        }

        dialogBinding.itemPdf.setOnClickListener {
           // findNavController().navigate(MainFragmentDirections.actionMainFragmentToExportDocumentFragment(id,folderName))
            getPdfFile(id, folderName) {
                openPdfFile(requireContext(), it)
            }
            bottomSheetDialog.dismiss()
        }

        dialogBinding.itemRename.setOnClickListener {
            renameFolderDialog(folderName, id)
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

        binding.leftBack.setOnClickListener {
            setSelectModeEnabled(false)
            adapter.clearSelectedItems()
        }
        binding.searchView.setOnSearchClickListener {
            binding.textView.visibility = View.GONE
        }
        binding.searchView.setOnCloseListener {
            binding.textView.visibility = View.VISIBLE
            false
        }
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                   searchedFolder(query)

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.isNullOrEmpty()){
                   setObservers()

                }else{
                    searchedFolder(newText)
                    binding.textView.visibility = View.GONE
                }
                return true
            }
        })

    }
    override fun onResume() {
        super.onResume()
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
        binding.searchView.isIconified = true
    }

    fun searchedFolder(folderName: String){
        lifecycleScope.launch {
          viewModel.searchFolderByName(folderName).collect{
               adapter.setAllFolder(it)
              adapter.notifyDataSetChanged()
           }
        }
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

    private fun renameFolderDialog(folderName : String,folderId: Long){
        val renameBinding = DialogRenameFolderBinding.inflate(layoutInflater)
        renameBinding.editRenameFolder.setText(folderName)
        MaterialAlertDialogBuilder(requireContext())
            .setView(renameBinding.root)
            .setPositiveButton("OK") { _, _ ->
                val newFolderName = renameBinding.editRenameFolder.text.toString()
                Toast.makeText(requireContext(),newFolderName,Toast.LENGTH_SHORT).show()
             viewModel.onEvent(FolderEvent.UpdateFolderName(newFolderName,folderId)){_,_ ->
             }

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun handleRenameEvent(){
        lifecycleScope.launch {
            viewModel.eventFlow.collectLatest { event ->
                when(event){

                    FolderViewModel.RenameFolderName.Success -> {
                        Toast.makeText(requireContext(),"Folder Renamed",Toast.LENGTH_SHORT).show()
                    }

                    is FolderViewModel.RenameFolderName.Error -> {
                        nameExistDialog(event.folderName,event.folderId)
                    }
                }
            }
        }
    }

    private fun nameExistDialog(folderName : String, folderId: Long){
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("The folder name already exist")
            .setPositiveButton("Try again"){_, _ ->
                renameFolderDialog(folderName,folderId)
            }
            .setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()

            }
            .show()

    }



    private fun setViewType(isLinear : Boolean){
        sharedPreferences.edit().putBoolean(IS_LINEAR, isLinear).apply()
        if (isLinear) {
            binding.rvMyFolderListView.layoutManager = LinearLayoutManager(requireContext())
            binding.gridImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.grid_view_icon))
        } else {
            binding.rvMyFolderListView.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.gridImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.list_view_icon))
        }
    }


    private fun addFolderAndNavigate(){
        val folder = MyFolderModel(
            folderName = "Docs"+System.currentTimeMillis().toString(),
            folderIcon = "",
            pageCount = 0,
            lastUpdated = System.currentTimeMillis()
        )
        viewModel.onEvent(FolderEvent.AddFolder(folder)){ folderId,folderName ->
            Toast.makeText(requireContext(),folderName,Toast.LENGTH_SHORT).show()
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
//        binding.allDocsHeadingTextView.isVisible = !isEnabled
        binding.fabMain.isVisible = !isEnabled
        binding.menuIcon.isVisible = !isEnabled
        binding.leftBack.isVisible = isEnabled
        binding.folderDeleteImageView.isVisible = isEnabled
        binding.selectAllImageView.isVisible = isEnabled
       // binding.closeImageView.isVisible = isEnabled
        adapter.notifyDataSetChanged()
    }

    private fun themeDialog() {
        val dialogBinding = DialogInkColorBinding.inflate(layoutInflater)
        dialogBinding.inkColorDialogTitle.setText("Select a theme")
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        val themesAdapter = ThemesAdapter { theme ->
            setThemeColor(theme.themeStyle)
            dialog.dismiss()
        }

        dialogBinding.inkColorDialogRecyclerView.adapter = themesAdapter
        dialogBinding.inkColorDialogRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 5)

        dialog.show()
    }

    private fun setThemeColor(themeId: Int) {
        appThemePref.edit().putInt(APP_THEME_PREF, themeId).apply()

        requireActivity().recreate()

    }

    //    private fun setIconColorByTheme(){
//        when(appThemePref.getInt(APP_THEME_PREF, R.style.AppTheme)){
//            R.style.AppTheme -> {
//                binding.checkFolderImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer))
//                binding.gridImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer))
//                binding.addFolderImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer))
//                binding.menuIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer))
//                binding.selectAllImageView.setColorFilter(ContextCompat.getColor(requireContext(),R.color.md_theme_light_tertiaryContainer))
//
//
//            }
//            R.style.AppTheme_Green -> {
//                binding.checkFolderImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer2))
//                binding.gridImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer2))
//                binding.addFolderImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer2))
//                binding.menuIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer2))
//                binding.selectAllImageView.setColorFilter(ContextCompat.getColor(requireContext(),R.color.md_theme_light_tertiaryContainer2))
//
//            }
//            R.style.AppTheme_pink -> {
//                binding.checkFolderImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer3))
//                binding.gridImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer3))
//                binding.addFolderImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer3))
//                binding.menuIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer3))
//                binding.selectAllImageView.setColorFilter(ContextCompat.getColor(requireContext(),R.color.md_theme_light_tertiaryContainer3))
//
//            }
//            R.style.AppTheme_teal -> {
//                binding.checkFolderImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer4))
//                binding.gridImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer4))
//                binding.addFolderImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer4))
//                binding.menuIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer4))
//                binding.selectAllImageView.setColorFilter(ContextCompat.getColor(requireContext(),R.color.md_theme_light_tertiaryContainer4))
//
//            }
//
//            R.style.AppTheme_purple -> {
//                binding.checkFolderImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer5))
//                binding.gridImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer5))
//                binding.addFolderImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer5))
//                binding.menuIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.md_theme_light_tertiaryContainer5))
//                binding.selectAllImageView.setColorFilter(ContextCompat.getColor(requireContext(),R.color.md_theme_light_tertiaryContainer5))
//            }
//
//        }
    //  }
    private fun setIconColorByTheme() {

        val colorResId = when (appThemePref.getInt(APP_THEME_PREF, R.style.AppTheme_Green)) {
            R.style.AppTheme -> R.color.md_theme_light_tertiaryContainer
            R.style.AppTheme_Green -> R.color.md_theme_light_tertiaryContainer2
            R.style.AppTheme_pink -> R.color.md_theme_light_tertiaryContainer3
            R.style.AppTheme_teal -> R.color.md_theme_light_tertiaryContainer4
            R.style.AppTheme_purple -> R.color.md_theme_light_tertiaryContainer5
            else -> R.color.md_theme_light_tertiaryContainer2
        }

        val color = ContextCompat.getColor(requireContext(), colorResId)

        with(binding) {
          //  checkFolderImageView.setColorFilter(color)
           // gridImageView.setColorFilter(color)
            //addFolderImageView.setColorFilter(color)
            menuIcon.setColorFilter(color)
            //selectAllImageView.setColorFilter(color)
            leftBack.setColorFilter(color)
        }
    }

}