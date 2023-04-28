package com.elkdocs.handwritter.presentation.folder_screen

import android.app.usage.UsageEvents.Event
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.elkdocs.handwritter.databinding.FragmentMainBinding
import com.elkdocs.handwritter.domain.model.MyFolderModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainFragment : Fragment() {
    
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: FolderAdapter
    private val viewModel: FolderViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater)
        val state = viewModel.state.value
        
        adapter = FolderAdapter{
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToPageViewerFragment(it))
        }
        binding.rvMyFolderListView.adapter = adapter
        binding.rvMyFolderListView.layoutManager = LinearLayoutManager(requireContext())
        
        binding.fabMain.setOnClickListener {

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
        
        setObservers()
        
        return binding.root
    }
    
    private fun setObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allFolders.collect {
                    binding.noDocuments.isVisible = it.isEmpty()
                    adapter.setAllFolder(it)
                }
            }
        }
    }
    
}