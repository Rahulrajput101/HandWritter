package com.elkdocs.handwritter.presentation.page_edit_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.FragmentPageEditBinding
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.util.Constant
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PageEditFragment : Fragment() {
     private lateinit var binding : FragmentPageEditBinding
     private val navArgs : PageEditFragmentArgs by navArgs()
     private val viewModel : PageEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val page =navArgs.pageDetail

        viewModel.setPageEditState(MyPageModel.fromMyPageModel(page))
        val pageEditState = viewModel.state.value
        binding = FragmentPageEditBinding.inflate(layoutInflater)

//        adapter = ViewPagerAdapter{updatedText ->
//            viewModel.setPageEditState(
//                pageEditState.copy(
//                    notesText = updatedText
//                )
//            )
//            Toast.makeText(requireContext(), viewModel.state.value.notesText,Toast.LENGTH_SHORT).show()
//        }
//        binding.myViewPager.adapter = adapter

        fontStyleAdapter()
        fontSizeAdapter()
        addLineAdapter()
        lineColorAdapter()
        //setObserver()


        BottomSheetBehavior.from(binding.bottomSheetLayout).apply {
            peekHeight = 100
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        return binding.root
    }

    private fun fontStyleAdapter() {
        val fontStyles = resources.getStringArray(R.array.font_styles_array)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down, fontStyles)
        binding.fontStyleAutoComplete.setAdapter(arrayAdapter)
        binding.fontStyleAutoComplete.setOnItemClickListener{parent, view, position, id ->
            val fontStyle = parent.getItemAtPosition(position).toString()
            viewModel.setFontStyleItem(fontStyle)
            Toast.makeText(requireContext(),fontStyle, Toast.LENGTH_SHORT).show()
            updateFontStyle(Constant.FONT_STYLES_MAP[fontStyle])
        }
    }
    private fun updateFontStyle(fontResourceId: Int?) {
        if (fontResourceId != null) {
            val typeface = ResourcesCompat.getFont(requireContext(), fontResourceId)
            binding.ivTextEditView.typeface = typeface
            viewModel.setPageEditState(viewModel.state.value.copy(fontStyle = fontResourceId))
        }
    }

    private fun fontSizeAdapter() {
        val fontStyles = resources.getStringArray(R.array.font_sizes_array)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down, fontStyles)
        binding.fontSizeAutoComplete.setAdapter(arrayAdapter)
        binding.fontSizeAutoComplete.setOnItemClickListener{parent, view, position, id ->
            val fontSize = parent.getItemAtPosition(position).toString()
            viewModel.setFontSizeItem(fontSize)
            Toast.makeText(requireContext(), viewModel.fontSizeItem,Toast.LENGTH_SHORT).show()
            updateFontSize(Constant.FONT_SIZES_MAP[fontSize])
        }
    }
    private fun updateFontSize(fontSizeValue: Float?){
        if(fontSizeValue!=null){
            binding.ivTextEditView.textSize = fontSizeValue
        }
    }

    private fun addLineAdapter() {
        val fontStyles = resources.getStringArray(R.array.add_line_array)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down, fontStyles)
        binding.addLinesAutoComplete.setAdapter(arrayAdapter)
        binding.addLinesAutoComplete.setOnItemClickListener{parent, view, position, id ->
            val addLine = parent.getItemAtPosition(position).toString()
            viewModel.setAddLineItem(addLine)
            Toast.makeText(requireContext(), viewModel.addLineItem,Toast.LENGTH_SHORT).show()
            updateLine(Constant.ADD_LINE_MAP[addLine])
        }
    }
    private fun updateLine(hasLine : Boolean?){
        if(hasLine != null){
            Toast.makeText(requireContext(), viewModel.addLineItem,Toast.LENGTH_SHORT).show()
        }
    }

    private fun lineColorAdapter() {
        val fontStyles = resources.getStringArray(R.array.line_color_array)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down, fontStyles)
        binding.lineColorAutoComplete.setAdapter(arrayAdapter)
        binding.lineColorAutoComplete.setOnItemClickListener{parent, view, position, id ->
            val lineColor = parent.getItemAtPosition(position).toString()
            viewModel.setLineColorItem(lineColor)
            Toast.makeText(requireContext(), viewModel.lineColorItem,Toast.LENGTH_SHORT).show()
            updateLineColor(Constant.LINE_COLOR_MAP[lineColor])
        }
    }

    private fun updateLineColor(color: Int?) {
        if(color != null){
            Toast.makeText(requireContext(), viewModel.lineColorItem,Toast.LENGTH_SHORT).show()
        }
    }


    //    private fun setObserver() {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.allPages.collect {
//                    adapter.setAllPages(it)
//                }
//            }
//        }
//    }
}