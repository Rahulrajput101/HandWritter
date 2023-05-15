package com.elkdocs.handwritter.presentation.folder_screen

import android.app.usage.UsageEvents.Event
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import com.elkdocs.handwritter.databinding.FragmentMainBinding
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.presentation.MainActivity
import com.elkdocs.handwritter.presentation.page_viewer_screen.PageViewerEvent
import com.elkdocs.handwritter.util.Constant.IS_LINEAR
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        val isLinear = sharedPreferences.getBoolean(IS_LINEAR, false)
        binding.menuIcon.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }
        
        adapter = FolderAdapter(
            onFolderClick = {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToPageViewerFragment(it))
            }
            , onFolderLongClick = {
                if (!adapter.isSelectModeEnabled) {
                    setSelectModeEnabled(true)
                }
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
                onFolderClick = {
                    findNavController().navigate(MainFragmentDirections.actionMainFragmentToPageViewerFragment(it))
                },
                onFolderLongClick = {
                    if (!adapter.isSelectModeEnabled) {
                        setSelectModeEnabled(true)
                    }
                },
                isLinear = newIsLinear
            )
            binding.rvMyFolderListView.adapter = adapter
            setObservers()
        }
        setClickListeners()


        
        return binding.root
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


    }
    private fun showDeleteAllDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete selected items?")
            .setPositiveButton("Delete") { dialog, which ->
                adapter.selectedItems.let {
                    if (it.isNotEmpty()) {
                        it.forEach { folder ->
                           viewModel.onEvent(FolderEvent.DeleteFolderWithPages(folder)){
                               Toast.makeText(requireContext(),"$it",Toast.LENGTH_SHORT).show()
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
        viewModel.onEvent(FolderEvent.AddFolder(folder)){
            it?.let {id ->
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToPageViewerFragment(id))
            }
        }
    }


    
    private fun setObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allFolders.collect {
                    val sortedList = it.reversed()
                    binding.noDocuments.isVisible = sortedList.isEmpty()
                    adapter.setAllFolder(sortedList)
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