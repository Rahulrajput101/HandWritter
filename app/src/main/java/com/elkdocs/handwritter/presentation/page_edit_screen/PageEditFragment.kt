package com.elkdocs.handwritter.presentation.page_edit_screen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.FragmentPageEditBinding
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.util.Constant
import com.elkdocs.handwritter.util.OtherUtility.provideBackgroundColorPrimary
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        binding = FragmentPageEditBinding.inflate(layoutInflater)
        //Toast.makeText(requireContext(),pageEditState.notesText,Toast.LENGTH_SHORT).show()
        val primaryColor = provideBackgroundColorPrimary(requireContext())
        binding.toolbarEditFileActivity.setBackgroundColor(primaryColor)

        val page =navArgs.pageDetail
        viewModel.setPageEditState(MyPageModel.fromMyPageModel(page))

        setInitialValues(page)
        fontStyleAdapter()
        fontSizeAdapter()
        addLineAdapter()
        lineColorAdapter()

        binding.editBackButton.setOnClickListener{
            findNavController().navigateUp()
        }
        binding.editForwardButton.setOnClickListener {
            val noteText = binding.ivTextEditView.text.toString()
            if(noteText.isNotEmpty()){
                viewModel.onEvent(PageEditEvent.UpdateNote(noteText))
            }
            viewModel.onEvent(PageEditEvent.UpdatePage)
            Toast.makeText(requireContext(),"saved",Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        BottomSheetBehavior.from(binding.bottomSheetLayout).apply {
            peekHeight = 100
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        return binding.root
    }

    private fun setInitialValues(page : MyPageModel) {
        binding.ivTextEditView.setText(page.notesText)
        if(page.fontStyle != null ){
            updateFontStyle(page.fontStyle)
        }
        //
    }

    private fun fontStyleAdapter() {
        val fontStyles = resources.getStringArray(R.array.font_styles_array)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down, fontStyles)
        binding.fontStyleAutoComplete.setAdapter(arrayAdapter)
        binding.fontStyleAutoComplete.setOnItemClickListener{parent, view, position, id ->
            val fontStyle = parent.getItemAtPosition(position).toString()
            Toast.makeText(requireContext(),fontStyle, Toast.LENGTH_SHORT).show()
            updateFontStyle(Constant.FONT_STYLES_MAP[fontStyle])
        }
    }
    private fun updateFontStyle(fontResourceId: Int?) {
        if (fontResourceId != null) {
            val typeface = ResourcesCompat.getFont(requireContext(), fontResourceId)
            binding.ivTextEditView.typeface = typeface
            binding.demoStyleTextView.typeface = typeface
            viewModel.onEvent(PageEditEvent.UpdateFontStyle(fontResourceId))
        }
    }

    private fun fontSizeAdapter() {
        val fontStyles = resources.getStringArray(R.array.font_sizes_array)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down, fontStyles)
        binding.fontSizeAutoComplete.setAdapter(arrayAdapter)
        binding.fontSizeAutoComplete.setOnItemClickListener{parent, view, position, id ->
            val fontSize = parent.getItemAtPosition(position).toString()
            Toast.makeText(requireContext(),fontSize,Toast.LENGTH_SHORT).show()
            updateFontSize(Constant.FONT_SIZES_MAP[fontSize])
        }
    }
    private fun updateFontSize(fontSizeValue: Float?){
        if(fontSizeValue!=null){
            binding.ivTextEditView.textSize = fontSizeValue
            viewModel.onEvent(PageEditEvent.UpdateFontSize(fontSizeValue))
        }
    }

    private fun addLineAdapter() {
        val fontStyles = resources.getStringArray(R.array.add_line_array)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down, fontStyles)
        binding.addLinesAutoComplete.setAdapter(arrayAdapter)
        binding.addLinesAutoComplete.setOnItemClickListener{parent, view, position, id ->
            val addLine = parent.getItemAtPosition(position).toString()
            Toast.makeText(requireContext(),addLine,Toast.LENGTH_SHORT).show()
            updateLine(Constant.ADD_LINE_MAP[addLine])
        }
    }
    private fun updateLine(hasLine : Boolean?){
        if(hasLine != null){
            when(hasLine){
                true -> {
                    val width = binding.ivImageEditView.width
                    val height = binding.ivImageEditView.height
                    val bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    viewModel.onEvent(PageEditEvent.UpdateAddLine(true))
                    viewModel.onEvent(PageEditEvent.DrawLine(canvas))
                    binding.ivImageEditView.setImageBitmap(bitmap)
                }
                false -> {
                    Toast.makeText(requireContext(), "$hasLine",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun lineColorAdapter() {
        val fontStyles = resources.getStringArray(R.array.line_color_array)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down, fontStyles)
        binding.lineColorAutoComplete.setAdapter(arrayAdapter)
        binding.lineColorAutoComplete.setOnItemClickListener{parent, view, position, id ->
            val lineColor = parent.getItemAtPosition(position).toString()
            Toast.makeText(requireContext(), lineColor,Toast.LENGTH_SHORT).show()
            updateLineColor(Constant.LINE_COLOR_MAP[lineColor])
        }
    }

    private fun updateLineColor(color: Int?) {
        if(color != null){
            Toast.makeText(requireContext(),"$color",Toast.LENGTH_SHORT).show()
            viewModel.onEvent(PageEditEvent.UpdateLineColor(color))
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