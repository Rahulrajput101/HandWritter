package com.elkdocs.handwritter.presentation.page_edit_screen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnLayout
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.FragmentPageEditBinding
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.util.Constant
import com.elkdocs.handwritter.util.Constant.REVERSE_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.OtherUtility.spToPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PageEditFragment : Fragment() {
    private lateinit var binding: FragmentPageEditBinding
    private val navArgs: PageEditFragmentArgs by navArgs()
    private val viewModel: PageEditViewModel by viewModels()
    private lateinit var pageColorAdapter: PageColorAdapter

    private lateinit var edtPageLayoutView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment 2
        binding = FragmentPageEditBinding.inflate(layoutInflater)
        //taking the saved details of the page for setup the ui
        val pageArgs = navArgs.pageDetail
        viewModel.setPageEditState(MyPageModel.fromMyPageModel(pageArgs))

        binding.edtPageLayout.doOnLayout {
            edtPageLayoutView = it
            setInitialValues(pageArgs, it)
            languageAdapter()
            fontStyleAdapter()
            fontSizeAdapter()
            lineColorAdapter()
            wordSpacing()
            lineWordSpacing(it)

        }

        horizontalScrollViewItems()
        pageColorAdapter()

        binding.editBackButton.setOnClickListener { findNavController().navigateUp() }

        //This will first saved the user input in database and then it will go to the viewer screen
        binding.editForwardButton.setOnClickListener {
            binding.ivTextEditView.clearFocus()
            val bitmap = binding.edtPageLayout.drawToBitmap()
            viewModel.onEvent(PageEditEvent.UpdateBitmap(bitmap))
            val noteText = viewModel.state.value.notesText
            if (noteText.isNotEmpty()) {
                viewModel.onEvent(PageEditEvent.UpdateNote(noteText))
            }
            viewModel.onEvent(PageEditEvent.UpdatePage)
            Toast.makeText(requireContext(), "saved", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        //set up of bottom sheet
        BottomSheetBehavior.from(binding.bottomSheetLayout).apply {
            peekHeight = 100
            state = BottomSheetBehavior.STATE_COLLAPSED
        }


        return binding.root
    }

    private fun horizontalScrollViewItems() {

        binding.boldText.setOnClickListener(textFormatClickListener)
        binding.italicText.setOnClickListener(textFormatClickListener)
        binding.underlineText.setOnClickListener {
            val isUnderlined = viewModel.state.value.underline
            updateUnderlineText(!isUnderlined)


        }
    }

    private fun updateUnderlineText(underline: Boolean) {
        val start =binding.ivTextEditView.selectionStart
        val end = binding.ivTextEditView.selectionEnd
        if(start != end){
            updateSelectedUnderlineText(start,end)
        } else {
            if (underline) {
                // Add underline
                binding.ivTextEditView.paintFlags =binding.ivTextEditView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            } else {
                // Remove underline
                binding.ivTextEditView.paintFlags = binding.ivTextEditView.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            }
        }

        viewModel.onEvent(PageEditEvent.UpdateUnderLine(underline))
    }


   private fun updateSelectedUnderlineText(  start : Int ,end : Int ){

        val spannableString = SpannableString(binding.ivTextEditView.text)
        spannableString.setSpan(
            UnderlineSpan(),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        Toast.makeText(requireContext(),spannableString,Toast.LENGTH_SHORT).show()
        binding.ivTextEditView.setText(spannableString)
    }



    //This will called when user select one of the font type and it will update the font type and ui for selection
    private val textFormatClickListener = View.OnClickListener { view ->
        val isBold = binding.ivTextEditView.typeface.isBold
        val isItalic = binding.ivTextEditView.typeface.isItalic
        when (view.id) {
            R.id.bold_text -> {
                binding.italicText.setTextColor(Color.BLACK)
                updateFontType(viewModel.state.value.fontStyle, if (isBold) Typeface.NORMAL else Typeface.BOLD)
                if (!isBold) {
                    binding.boldText.setTextColor(Color.BLUE)
                } else {
                    binding.boldText.setTextColor(Color.BLACK)
                }
            }

            R.id.italic_text -> {
                binding.boldText.setTextColor(Color.BLACK)
                updateFontType(viewModel.state.value.fontStyle, if (isItalic) Typeface.NORMAL else Typeface.ITALIC)
                if (!isItalic) {
                    binding.italicText.setTextColor(Color.BLUE)

                } else {
                    binding.italicText.setTextColor(Color.BLACK)
                }
            }
        }
    }


    private fun setInitialValues(page: MyPageModel, view: View) {
        //setting up initial text
        if (page.notesText.isEmpty()) {
            //This will fill the edit text with space
            //creatingEmptyLine()
        } else {
            binding.ivTextEditView.apply {
                setText(page.notesText)
                letterSpacing = page.letterSpace
            }
        }

        //setting up seekbars
        binding.seekbarForLetterAndWord.progress = (page.letterSpace * 100).toInt()
        binding.seekbarForLineAndWord.progress =
            //((page.textAndLineSpace - 0.095f) / 0.020f * 100).toInt()
            ((page.textAndLineSpace + 5f) / 30f * 100).toInt()

        when (page.fontType) {
            Typeface.NORMAL -> Toast.makeText(requireContext(), "Normal", Toast.LENGTH_SHORT).show()
            Typeface.BOLD -> binding.boldText.setTextColor(Color.BLUE)
            Typeface.ITALIC -> binding.italicText.setTextColor(Color.BLUE)
        }

        binding.fontSizeAutoComplete.setText("${page.fontSize}")
        binding.fontStyleAutoComplete.setText(REVERSE_FONT_STYLE_MAP[page.fontStyle])
        binding.ivImageEditView.setBackgroundColor(page.pageColor)
        updateFontStyle(page.fontStyle)
        updateFontType(page.fontStyle, page.fontType)
        updateFontSize(page.fontSize)
        updateLine(page.addLines, page.fontSize, page.lineColor, view)

        //Horizontal scroll view
        //Initially we are not give the underline
         updateUnderlineText(page.underline)
    }

    //setting demo text
    private fun demoTextLine(){
        val width = binding.ivImageDemoView.measuredWidth
        val height = binding.ivImageDemoView.measuredHeight
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        demoViewHorizontalLine(canvas,viewModel.state.value.fontSize, viewModel.state.value.lineColor)
        binding.ivImageDemoView.setImageBitmap(bitmap)
    }


    private fun pageColorAdapter() {
        pageColorAdapter = PageColorAdapter(
            onPageColorClick = { pageColor, adapterPostion ->
                binding.ivImageEditView.setBackgroundColor(pageColor)
                viewModel.onEvent(PageEditEvent.UpdatePageColor(pageColor))
            }
        )
        binding.selectPageColorRecyclerView.adapter = pageColorAdapter
        binding.selectPageColorRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.HORIZONTAL,
            false
        )
    }

    private fun fontStyleAdapter() {
        val fontStyles = resources.getStringArray(R.array.font_styles_array)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_down, fontStyles)
        binding.fontStyleAutoComplete.setAdapter(arrayAdapter)
        binding.fontStyleAutoComplete.setOnItemClickListener { parent, view, position, id ->
            val fontStyle = parent.getItemAtPosition(position).toString()
            binding.boldText.setTextColor(Color.BLACK)
            binding.italicText.setTextColor(Color.BLACK)
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

    private fun languageAdapter() {
        val fontStyles = resources.getStringArray(R.array.languages_array)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_down, fontStyles)
        binding.languageAutoComplete.setAdapter(arrayAdapter)
        binding.languageAutoComplete.setOnItemClickListener { parent, view, position, id ->
            val fontStyle = parent.getItemAtPosition(position).toString()
            binding.boldText.setTextColor(Color.BLACK)
            binding.italicText.setTextColor(Color.BLACK)
            updateLanguage(Constant.LANGUAGE_MAP[fontStyle])
        }
    }

    private fun updateLanguage(fontResourceId: Int?) {
        if (fontResourceId != null) {
            Toast.makeText(requireContext(), "$fontResourceId", Toast.LENGTH_SHORT).show()
            val typeface = ResourcesCompat.getFont(requireContext(), fontResourceId)
            binding.ivTextEditView.typeface = typeface
            binding.demoStyleTextView.typeface = typeface
            viewModel.onEvent(PageEditEvent.UpdateFontStyle(fontResourceId))
        }
    }

    private fun fontSizeAdapter() {
        val fontStyles = resources.getStringArray(R.array.font_sizes_array)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_down, fontStyles)
        binding.fontSizeAutoComplete.setAdapter(arrayAdapter)
        binding.fontSizeAutoComplete.setOnItemClickListener { parent, view, position, id ->
            val fontSize = parent.getItemAtPosition(position).toString()
            updateFontSize(Constant.FONT_SIZES_MAP[fontSize])
        }
    }
    private fun updateFontSize(fontSizeValue: Float?) {
        if (fontSizeValue != null) {
            binding.ivTextEditView.textSize = fontSizeValue
            binding.demoStyleTextView.textSize = fontSizeValue
            viewModel.onEvent(PageEditEvent.UpdateFontSize(fontSizeValue))
            demoTextLine()
            updateLine(viewModel.state.value.addLines, fontSizeValue, viewModel.state.value.lineColor, edtPageLayoutView)
        }
    }

    private fun lineColorAdapter() {
        val fontStyles = resources.getStringArray(R.array.line_color_array)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_down, fontStyles)
        binding.lineColorAutoComplete.setAdapter(arrayAdapter)
        binding.lineColorAutoComplete.setOnItemClickListener { parent, view, position, id ->
            val lineColor = parent.getItemAtPosition(position).toString()
            updateLineColor(Constant.LINE_COLOR_MAP[lineColor])
        }
    }

    private fun updateLineColor(color: Int?) {
        if (color != null) {
            Toast.makeText(requireContext(), "$color", Toast.LENGTH_SHORT).show()
            viewModel.onEvent(PageEditEvent.UpdateLineColor(color))
            updateLine(viewModel.state.value.addLines, viewModel.state.value.fontSize, color, edtPageLayoutView)
        }
    }


    private fun updateFontType(fontStyle: Int, fontType: Int) {
        val typeface = ResourcesCompat.getFont(requireContext(), fontStyle)
        binding.ivTextEditView.setTypeface(typeface, fontType)
        viewModel.onEvent(PageEditEvent.UpdateFontType(fontType))
    }

    private fun updateLine(hasLine: Boolean?, fontSize: Float, lineColor: Int, view: View) {
        if (hasLine != null) {
            when (hasLine) {
                true -> {
                    //updating demo text

                    val width = view.measuredWidth
                    val height = view.measuredHeight
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    viewModel.onEvent(PageEditEvent.UpdateAddLine(true))
                    drawLines(canvas, fontSize, lineColor)
                    verticalLine(canvas)
                    binding.ivImageEditView.setImageBitmap(bitmap)
                     binding.demoParentLayout.doOnLayout {
                         demoTextLine()
                     }
                }

                false -> {
                    viewModel.onEvent(PageEditEvent.UpdateAddLine(false))
                }
            }
        }
    }


    private fun verticalLine(canvas: Canvas) {
        val paint = Paint()
        //for blue color we are giving different color to vertical line
        if (viewModel.state.value.lineColor == 0xFFB2B1D3.toInt()) {
            paint.color = Color.parseColor("#D1C2E1")
        } else {
            paint.color = viewModel.state.value.lineColor
        }

        paint.strokeWidth = 2f

        // Draw vertical line
        val x = canvas.width * 0.15f // Change this value to adjust the x-coordinate of the line
        canvas.drawLine(x.toFloat(), 0f, x.toFloat(), canvas.height.toFloat(), paint)
    }

    private fun demoViewHorizontalLine(canvas: Canvas, fontSize: Float , lineColor: Int){

        val lineSpacing = spToPx(fontSize, requireContext()).toFloat()
        binding.demoStyleTextView.setLineSpacing(lineSpacing,0f)

        val linePaint = Paint()
        linePaint.strokeWidth = 2f
        linePaint.color = lineColor

        val paddingTop = viewModel.state.value.textAndLineSpace

        for(i in paddingTop.toInt() until canvas.height step lineSpacing.toInt()){
            linePaint.color = lineColor
            canvas.drawLine(0f, i.toFloat(), canvas.width.toFloat(), i.toFloat() , linePaint)
        }

    }


    private fun drawLines(canvas: Canvas, fontSize: Float, lineColor: Int) {
        val lineSpacing = spToPx(fontSize, requireContext()).toFloat() // or any other ratio you prefer
        binding.ivTextEditView.setLineSpacing(lineSpacing, 0f)

        val linePaint = Paint()
        linePaint.strokeWidth = 2f
        linePaint.color = lineColor

        //val paddingTop = canvas.height * (viewModel.state.value.textAndLineSpace )
        val pageHeaderSpace = 0.10
        val paddingTop = (canvas.height * pageHeaderSpace) + viewModel.state.value.textAndLineSpace

        for (i in paddingTop.toInt() until canvas.height step lineSpacing.toInt()) {
            if (i == paddingTop.toInt()) {
                //for blue color we are giving different color to first line
                if(lineColor == 0xFFB2B1D3.toInt()){
                    linePaint.color = Color.parseColor("#D1C2E1")
                }else {
                    linePaint.color = lineColor
                }

            } else {
                linePaint.color = lineColor
            }

            canvas.drawLine(0f, i.toFloat(), canvas.width.toFloat(), i.toFloat(), linePaint)
        }
    }


  //it will give the spacing between letters and word
    private fun wordSpacing() {
        binding.seekbarForLetterAndWord.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val spacingValue = progress.toFloat() / 100
                binding.ivTextEditView.letterSpacing = spacingValue
                binding.demoStyleTextView.letterSpacing = spacingValue
                viewModel.onEvent(PageEditEvent.UpdateLetterSpacing(spacingValue))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    //it will give the spacing between line and text
        private fun lineWordSpacing(view: View){
        binding.seekbarForLineAndWord.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //val textAndLineSpacingValue = 0.095f + (0.020f * progress / 100)
                val textAndLineSpacingValue = (-5f + (30f * progress / 100))
                viewModel.onEvent(PageEditEvent.UpdateTextAndLineSpacing(textAndLineSpacingValue))
                val currentState = viewModel.state.value
                updateLine(currentState.addLines,currentState.fontSize,currentState.lineColor,view)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun creatingEmptyLine(){
        val text = StringBuilder()
        text.append("Enter the text")
        val lineHeight = binding.ivTextEditView.lineHeight
        val editTextHeight = binding.ivTextEditView.height

        // Total number of lines in edit text
        val totalLines = editTextHeight / lineHeight
        for( i in 0 until totalLines){
            for(j in 0 until binding.ivTextEditView.width){
                text.append(" ")
            }
            //changing the line until he reaches at the last line
            if(i < totalLines - 1){
                text.append("\n")
            }
        }
        //finally setting up the result to edit text
        binding.ivTextEditView.setText(text)
    }

}