package com.elkdocs.handwritter.presentation.page_edit_screen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.FragmentPageEditBinding
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.util.Constant
import com.elkdocs.handwritter.util.Constant.PURPLE_LINE_COLOR
import com.elkdocs.handwritter.util.Constant.REVERSE_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.OtherUtility.provideBackgroundColorPrimary
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PageEditFragment : Fragment() {
     private lateinit var binding : FragmentPageEditBinding
     private val navArgs : PageEditFragmentArgs by navArgs()
     private val viewModel : PageEditViewModel by viewModels()
     private lateinit var pageColorAdapter : PageColorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPageEditBinding.inflate(layoutInflater)

        val primaryColor = provideBackgroundColorPrimary(requireContext())
        binding.toolbarEditFileActivity.setBackgroundColor(primaryColor)

        val pageArgs =navArgs.pageDetail
        viewModel.setPageEditState(MyPageModel.fromMyPageModel(pageArgs))

        setInitialValues(pageArgs)
        pageColorAdapter()
        fontStyleAdapter()
        fontSizeAdapter()
        addLineAdapter()
        lineColorAdapter()


        binding.boldText.setOnClickListener(textFormatClickListener)
        binding.italicText.setOnClickListener(textFormatClickListener)

        binding.editBackButton.setOnClickListener{ findNavController().navigateUp() }
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

    private fun pageColorAdapter() {
        pageColorAdapter = PageColorAdapter(
            onPageColorClick = {pageColor,adapterPostion ->
                binding.ivImageEditView.setBackgroundColor(pageColor)
                viewModel.onEvent(PageEditEvent.UpdatePageColor(pageColor))
            }
        )

        binding.selectPageColorRecyclerView.adapter = pageColorAdapter
        binding.selectPageColorRecyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
    }

    private val textFormatClickListener = View.OnClickListener { view ->
        val isBold = binding.ivTextEditView.typeface.isBold
        val isItalic = binding.ivTextEditView.typeface.isItalic
        when (view.id) {
            R.id.bold_text -> {
                binding.italicText.setTextColor(Color.BLACK)
                updateFontType(viewModel.state.value.fontStyle,if (isBold) Typeface.NORMAL else Typeface.BOLD)
                if(!isBold){
                    binding.boldText.setTextColor(Color.BLUE)
                }else{
                    binding.boldText.setTextColor(Color.BLACK)
                }
            }

            R.id.italic_text -> {
                binding.boldText.setTextColor(Color.BLACK)
                updateFontType(viewModel.state.value.fontStyle,if (isItalic)Typeface.NORMAL else Typeface.ITALIC)
                if(!isItalic){
                    binding.italicText.setTextColor(Color.BLUE)

                }else{
                    binding.italicText.setTextColor(Color.BLACK)
                }
            }
        }
    }


    private fun updateFontType(fontStyle : Int,fontType: Int){
        val typeface = ResourcesCompat.getFont(requireContext(),fontStyle)
        binding.ivTextEditView.setTypeface(typeface,fontType)
        viewModel.onEvent(PageEditEvent.UpdateFontType(fontType))
    }


    private fun setInitialValues(page : MyPageModel) {
        binding.ivTextEditView.setText(page.notesText)
        when(page.fontType){
            Typeface.NORMAL -> Toast.makeText(requireContext(),"Normal",Toast.LENGTH_SHORT).show()
            Typeface.BOLD -> binding.boldText.setTextColor(Color.BLUE)
            Typeface.ITALIC -> binding.italicText.setTextColor(Color.BLUE)
        }
        binding.fontSizeAutoComplete.setText("${page.fontSize}")
        binding.fontStyleAutoComplete.setText(REVERSE_FONT_STYLE_MAP[page.fontStyle])
        binding.addLinesAutoComplete.setText(if(page.addLines) "on" else "off")
        binding.ivImageEditView.setBackgroundColor(page.pageColor)
        updateFontStyle(page.fontStyle)
        updateLine(page.addLines)
        updateFontType(page.fontStyle,page.fontType)
    }

    private fun fontStyleAdapter() {
        val fontStyles = resources.getStringArray(R.array.font_styles_array)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down, fontStyles)
        binding.fontStyleAutoComplete.setAdapter(arrayAdapter)
        binding.fontStyleAutoComplete.setOnItemClickListener{parent, view, position, id ->
            val fontStyle = parent.getItemAtPosition(position).toString()
            binding.boldText.setTextColor(Color.BLACK)
            binding.italicText.setTextColor(Color.BLACK)
            Toast.makeText(requireContext(),fontStyle, Toast.LENGTH_SHORT).show()
            updateFontStyle(Constant.FONT_STYLES_MAP[fontStyle])
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

    private fun updateFontStyle(fontResourceId: Int?) {
        if (fontResourceId != null) {
            Toast.makeText(requireContext(), "$fontResourceId",Toast.LENGTH_SHORT).show()
            val typeface = ResourcesCompat.getFont(requireContext(), fontResourceId)
            binding.ivTextEditView.typeface = typeface
            binding.demoStyleTextView.typeface = typeface
            viewModel.onEvent(PageEditEvent.UpdateFontStyle(fontResourceId))
        }
    }

    private fun updateFontSize(fontSizeValue: Float?){
        if(fontSizeValue!=null){
            binding.ivTextEditView.textSize = fontSizeValue
            viewModel.onEvent(PageEditEvent.UpdateFontSize(fontSizeValue))
        }
        updateLine(viewModel.state.value.addLines)
    }

    private fun updateLine(hasLine : Boolean?){
        if(hasLine != null){
            when(hasLine){
                true -> {
                    val width = 1024
                    val height = 1845
                    val bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    viewModel.onEvent(PageEditEvent.UpdateAddLine(true))
                    //viewModel.onEvent(PageEditEvent.DrawLine(canvas))
                    drawLines(canvas,viewModel.state.value.fontSize,PURPLE_LINE_COLOR,binding.ivTextEditView)
                    verticalLine(canvas)
                    binding.ivImageEditView.setImageBitmap(bitmap)
                }
                false -> {
                    viewModel.onEvent(PageEditEvent.UpdateAddLine(false))
                }
            }
        }
    }



    private fun updateLineColor(color: Int?) {
        if(color != null){
            Toast.makeText(requireContext(),"$color",Toast.LENGTH_SHORT).show()
            viewModel.onEvent(PageEditEvent.UpdateLineColor(color))
        }
    }

    private fun verticalLine(canvas: Canvas){
        val paint = Paint()
        paint.color = Color.parseColor("#D1C2E1")
        paint.strokeWidth = 2f

// Draw horizontal line
//        val y = canvas.height * 0.10f // Change this value to adjust the y-coordinate of the line
//        canvas.drawLine(0f, y.toFloat(), canvas.width.toFloat(), y.toFloat(), paint)

        // Draw vertical line
        val x = canvas.width * 0.15f // Change this value to adjust the x-coordinate of the line
        canvas.drawLine(x.toFloat(), 0f, x.toFloat(), canvas.height.toFloat(), paint)
    }

    private fun drawLines(canvas: Canvas, fontSize: Float, lineColor: Int, editText: EditText?){

        val lineSpacing = fontSize * 3f // or any other ratio you prefer
        binding.ivTextEditView.setLineSpacing(lineSpacing,0f)
        val linePaint = Paint()
        linePaint.strokeWidth = 2f
        linePaint.color = lineColor

        val yOffset = (fontSize - lineSpacing) / 2 // Center the lines vertically

        val paddingTop = canvas.height * 0.10f

        for (i in paddingTop.toInt() until canvas.height step lineSpacing.toInt()) {
            if (i == paddingTop.toInt()) {
                linePaint.color = Color.parseColor("#D1C2E1")
            } else {
                linePaint.color = lineColor
            }

            canvas.drawLine(0f, i + yOffset, canvas.width.toFloat(), i + yOffset, linePaint)
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