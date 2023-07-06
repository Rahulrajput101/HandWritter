package com.elkdocs.notestudio.presentation.folder_screen

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.toBitmap
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
import com.elkdocs.notestudio.R
import com.elkdocs.notestudio.databinding.CustomPopupMenuBinding
import com.elkdocs.notestudio.databinding.DialogInkColorBinding
import com.elkdocs.notestudio.databinding.DialogRenameFolderBinding
import com.elkdocs.notestudio.databinding.FragmentMainBinding
import com.elkdocs.notestudio.domain.model.MyFolderModel
import com.elkdocs.notestudio.presentation.MainActivity
import com.elkdocs.notestudio.util.Constant.APP_THEME_PREF
import com.elkdocs.notestudio.util.Constant.IS_LINEAR
import com.elkdocs.notestudio.util.PdfUtility.createPdf
import com.elkdocs.notestudio.util.PdfUtility.openPdfFile
import com.elkdocs.notestudio.util.PdfUtility.sharePdf
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@AndroidEntryPoint
class MainFragment : Fragment(),MenuProvider {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: FolderAdapter
    private lateinit var toggle : ActionBarDrawerToggle
    private val viewModel: FolderViewModel by viewModels()
    private var menu : Menu? = null
    private var pageSize = 0

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
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
        val isLinear = sharedPreferences.getBoolean(IS_LINEAR, true)
        binding.menuIcon.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }

        adapter = FolderAdapter(
            onFolderClick = { folderId, folderName ->
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToPageViewerFragment(
                        folderId,
                        folderName
                    )
                )
            }, onFolderLongClick = {
                if (!adapter.isSelectModeEnabled) {
                    setSelectModeEnabled(true)
                }
            },
            onMoreOptionClick = { folderId, folderName, date, itemImageView ->
                popupMenu(folderId, folderName, date, itemImageView)
            },
            isLinear = isLinear
        )

        binding.rvMyFolderListView.adapter = adapter
        setViewType(isLinear)

        binding.navigationView.setNavigationItemSelectedListener {


            when (it.itemId) {
                R.id.item1 -> {
                    findNavController().navigate(MainFragmentDirections.actionMainFragmentToAboutFragment())
                }

                R.id.item2 -> {}
                R.id.item3 -> {}
                R.id.item4 -> {
                    themeDialog()
                }
            }
            true
        }


        binding.addFolderImageView.setOnClickListener { addFolderAndNavigate() }
        binding.fabMain.setOnClickListener { addFolderAndNavigate() }

        setObservers()

        binding.gridImageView.setOnClickListener {
            val newIsLinear = !sharedPreferences.getBoolean(IS_LINEAR, false)
            setViewType(newIsLinear)
            lifecycleScope.launch {


            adapter = FolderAdapter(
                onFolderClick = { folderId, folderName ->
                    findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToPageViewerFragment(
                            folderId,
                            folderName
                        )
                    )
                },
                onFolderLongClick = {
                    if (!adapter.isSelectModeEnabled) {
                        setSelectModeEnabled(true)
                    }
                },
                onMoreOptionClick = { folderId, folderName, date, itemImageView ->
                    //onShareClick(it)
                    popupMenu(folderId, folderName, date, itemImageView)

                },

                isLinear = newIsLinear
            )
            binding.rvMyFolderListView.adapter = adapter
            setObservers()
        }
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
                val successful = openPdfFile(requireContext(), it)
                if(!successful){
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(),"You don't have any app to open this file",Toast.LENGTH_SHORT).show()
                    }
                }
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
        renameBinding.editRenameFolder.requestFocus()

        MaterialAlertDialogBuilder(requireContext())
            .setView(renameBinding.root)
            .setPositiveButton("OK") { _, _ ->
                val newFolderName = renameBinding.editRenameFolder.text.toString()
             viewModel.onEvent(FolderEvent.UpdateFolderName(newFolderName,folderId)){_,_ ->
             }

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    renameBinding.editRenameFolder.requestFocus()
    renameBinding.editRenameFolder.postDelayed(
        {
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(renameBinding.editRenameFolder, InputMethodManager.SHOW_IMPLICIT)
        },
        200 // Delay in milliseconds
    )
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

                    else -> {
                        return@collectLatest
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
        val pageBitmap = ContextCompat.getDrawable(requireContext(), R.drawable.page_image)
            ?.toBitmap(1024,1833, Bitmap.Config.ARGB_8888)
        val folder = MyFolderModel(
            folderName = "Docs"+System.currentTimeMillis().toString(),
            folderIcon = pageBitmap!!,
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
                viewModel.allFolders.collect { foldersList ->
                    binding.noDocuments.isVisible = foldersList.isEmpty()
                    adapter.setAllFolder(foldersList.reversed())
                }
            }
        }

            lifecycleScope.launch() {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.allPages.collect { pageList ->
                        if(pageList.isNotEmpty()){
                            adapter.setPages(pageList)
                        }

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
        binding.fabMain.isVisible = !isEnabled
        binding.menuIcon.isVisible = !isEnabled
        binding.leftBack.isVisible = isEnabled
        binding.folderDeleteImageView.isVisible = isEnabled
        binding.selectAllImageView.isVisible = isEnabled
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
            menuIcon.setColorFilter(color)
            leftBack.setColorFilter(color)
        }
    }

}