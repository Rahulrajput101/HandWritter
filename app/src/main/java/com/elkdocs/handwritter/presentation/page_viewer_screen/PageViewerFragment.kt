package com.elkdocs.handwritter.presentation.page_viewer_screen

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.FragmentPageViewerBinding
import com.elkdocs.handwritter.domain.model.MyPageModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        adapter = PageViewerAdapter()
        viewModel.updateFolderId(navArgs.folderId)
        binding.rvPages.adapter = adapter
        binding.rvPages.layoutManager = GridLayoutManager(requireContext(),3)
        
        setClickListeners()
        setObserver()
        return binding.root
    }

    private fun setObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allPages.collect {
                    adapter.setAllPages(it)
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.fabImagePicker.setOnClickListener {
                val page = MyPageModel(
                    folderId = navArgs.folderId,
                    uriIndex = 0,
                    notesText = "",
                    fontSize = "",
                    fontStyle = 0,
                    charSpace = "",
                    wordSpace = "",
                    addHrLines = "",
                    addVrLines = "",
                    lineColor = ""
                )
                viewModel.onEvent(PageViewerEvent.AddPage(page))
        }
    }
    
}