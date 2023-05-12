package com.elkdocs.handwritter.presentation.folder_screen

import android.app.usage.UsageEvents.Event
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.FragmentMainBinding
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainFragment : Fragment(),MenuProvider {
    
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: FolderAdapter
    private lateinit var toggle : ActionBarDrawerToggle
    private val viewModel: FolderViewModel by viewModels()
    private var menu : Menu? = null
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater)
        val menuHost : MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)

        val state = viewModel.state.value
        toggle = ActionBarDrawerToggle(requireActivity(),binding.drawerLayout,R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.menuIcon.setOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }
        
        adapter = FolderAdapter{
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToPageViewerFragment(it))
        }

        binding.rvMyFolderListView.adapter = adapter
        binding.rvMyFolderListView.layoutManager = LinearLayoutManager(requireContext())

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
        
        return binding.root
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

}