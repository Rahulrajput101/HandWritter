package com.elkdocs.handwritter.presentation.page_edit_screen

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnLayout
import androidx.core.view.drawToBitmap
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.DialogInkColorBinding
import com.elkdocs.handwritter.databinding.FragmentPageEditBinding
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.presentation.page_edit_screen.PageEditState.Companion.alignmentOptions
import com.elkdocs.handwritter.presentation.page_edit_screen.PageEditState.Companion.fontSizeList
import com.elkdocs.handwritter.presentation.page_edit_screen.PageEditState.Companion.inputDateFormat
import com.elkdocs.handwritter.presentation.page_edit_screen.PageEditState.Companion.outputDateFormat
import com.elkdocs.handwritter.util.Constant
import com.elkdocs.handwritter.util.Constant.FONT_SIZES_MAP
import com.elkdocs.handwritter.util.Constant.INK_COLOR_MAP
import com.elkdocs.handwritter.util.Constant.REVERSE_FONT_SIZE_MAP
import com.elkdocs.handwritter.util.Constant.REVERSE_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.OtherUtility.spToPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_FADE
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class PageEditFragment : Fragment() {
    private lateinit var binding: FragmentPageEditBinding
    private val navArgs: PageEditFragmentArgs by navArgs()
    private val viewModel: PageEditViewModel by viewModels()
    private lateinit var pageColorAdapter: PageColorAdapter
    private lateinit var inkColorAdapter: InkColorAdapter
    private lateinit var edtPageLayoutView: View

    private var offsetX: Float = 0f
    private var offsetY: Float = 0f
    private var startX: Float = 0f
    private var startY: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment 2
        binding = FragmentPageEditBinding.inflate(layoutInflater)
        //taking the saved details of the page for setup the ui
        val pageArgs = navArgs.pageDetail
        viewModel.setPageEditState(MyPageModel.fromMyPageModel(pageArgs))
        Log.v("TAG","x =${pageArgs.dateTextViewX} , y = ${pageArgs.dateTextViewY}")

        binding.edtPageLayout.doOnLayout {
            edtPageLayoutView = it
            setInitialValues(pageArgs, it)
            languageAdapter()
            fontStyleAdapter()
            //fontSizeAdapter()
            lineColorAdapter()
            wordSpacing()
            lineWordSpacing(it)
            dateTextTouchListener()

        }

        horizontalScrollViewItems()
        pageColorAdapter()

        binding.editBackButton.setOnClickListener { findNavController().navigateUp() }

        //This will first saved the user input in database and then it will go to the viewer screen
        binding.editForwardButton.setOnClickListener {
            binding.ivTextEditView.clearFocus()
            val bitmap = binding.edtPageLayout.drawToBitmap()
            viewModel.onEvent(PageEditEvent.UpdateBitmap(bitmap))
            val noteText = binding.ivTextEditView.text.toString()
            if (noteText.isNotEmpty()) {
                viewModel.onEvent(PageEditEvent.UpdateNote(noteText))
            }
            Log.v("TAG","x =${viewModel.state.value.dateTextViewX} , y = ${viewModel.state.value.dateTextViewY}")
//            viewModel.onEvent(PageEditEvent.UpdatePage)
            lifecycleScope.launch {
                viewModel.upsertPage()
                findNavController().navigateUp()
            }
        }

        //set up of bottom sheet
        BottomSheetBehavior.from(binding.bottomSheetLayout).apply {
            peekHeight = 100
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        return binding.root
    }

    private fun pageNumberDialog() {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_page_number, null)
            MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("OK") { _, _ ->
                    val pageNumberEditText = dialogView.findViewById<EditText>(R.id.editPageNumber)
                    val pageNumber = pageNumberEditText.text.toString()
                    if(pageNumber.isNotEmpty()){
                        binding.pageNumberTextView.text = pageNumber
                        binding.addPageNuumberButton.setTextColor(Color.BLUE)
                        viewModel.onEvent(PageEditEvent.UpdatePageNumber(pageNumber))
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun dateTextTouchListener(){

        binding.dateText.setOnTouchListener { v, event ->
            val parentView = binding.edtPageLayout
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    offsetX = event.rawX - v.x
                    offsetY = event.rawY - v.y
                     startX = v.x
                     startY = v.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX - offsetX
                    val newY = event.rawY - offsetY

                    // Calculate the boundaries based on the parent view's dimensions
                    val minX = 0f
                    val maxX = parentView.width - v.width
                    val minY = 0f
                    val maxY = parentView.height - v.height

                    // Constrain the new coordinates within the boundaries
                    val constrainedX = newX.coerceIn(minX, maxX.toFloat())
                    val constrainedY = newY.coerceIn(minY, maxY.toFloat())

                    v.x = constrainedX
                    v.y = constrainedY
                }
                MotionEvent.ACTION_UP -> {

                    viewModel.onEvent(PageEditEvent.UpdateDateTextPosition(v.x + 50f,v.y))
                    // Implement any additional logic after dragging ends
                }
            }
            true
        }
    }


    private fun horizontalScrollViewItems() {
        textAlignmentDropDown()
        fontSizeDropDown()
        binding.boldText.setOnClickListener(textFormatClickListener)
        binding.italicText.setOnClickListener(textFormatClickListener)
        binding.underlineText.setOnClickListener {
            val isUnderlined = viewModel.state.value.underline
            updateUnderlineText(!isUnderlined)
        }
        binding.dateTextButton.setOnClickListener {
            updateDate(binding.dateText.text.toString())
        }

        binding.addPageNuumberButton.setOnClickListener {
            updatePageNumber(binding.pageNumberTextView.text.toString())
        }
        binding.inkColorIcon.setOnClickListener { inkColorDialog() }

    }

    private fun updateDatePosition(x : Float , y : Float){
        if(x != 0f || y != 0f){
            binding.dateText.x = x
            binding.dateText.y = y
        }
    }

    private fun updatePageNumber(pageNumber : String){
        if(pageNumber.isEmpty()){
            pageNumberDialog()
        }else {
            binding.addPageNuumberButton.setTextColor(Color.BLACK)
            binding.pageNumberTextView.text = ""
            viewModel.onEvent(PageEditEvent.UpdatePageNumber(""))
        }
    }


    private fun updateDate(addDate : String){
        if(addDate.isEmpty()){
            pickDate {
                val date = inputDateFormat.parse(it)
                date?.let {
                    val formattedDate = outputDateFormat.format(date)
                    binding.dateTextButton.setTextColor(Color.BLUE)
                   val x = binding.dateText.x
                    val y = binding.dateText.y
                    binding.dateText.text = formattedDate
                    viewModel.onEvent(PageEditEvent.UpdateDateTextPosition(x,y))
                    viewModel.onEvent(PageEditEvent.UpdateDate(formattedDate))
                    Snackbar.make(requireView(),"You can drag this date anywhere",Snackbar.ANIMATION_MODE_SLIDE).apply {
                        setBackgroundTint(Color.WHITE)
                        setTextColor(ContextCompat.getColor(requireContext(),R.color.seed))
                        animationMode = ANIMATION_MODE_FADE
                    }.show()
                }?: Toast.makeText(requireContext(), "Select Date ", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.dateTextButton.setTextColor(Color.BLACK)
            binding.dateText.text =""
            viewModel.onEvent(PageEditEvent.UpdateDateTextPosition(0f,0f))
            viewModel.onEvent(PageEditEvent.UpdateDate(""))
        }
    }


    private fun updateUnderlineText(underline: Boolean) {
            if (underline) {
                // Add underline
                binding.underlineText.setTextColor(Color.BLUE)
                binding.ivTextEditView.paintFlags =binding.ivTextEditView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            } else {
                // Remove underline
                binding.underlineText.setTextColor(Color.BLACK)
                binding.ivTextEditView.paintFlags = binding.ivTextEditView.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
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
                setTextColor(page.inkColor)
                letterSpacing = page.letterSpace
            }
            binding.demoStyleTextView.apply {
                setTextColor(page.inkColor)
                letterSpacing = page.letterSpace
            }
        }

        // Page number
        val pageNumberColor = if (page.pageNumber.isNotEmpty()) Color.BLUE else Color.BLACK
        binding.addPageNuumberButton.setTextColor(pageNumberColor)
        binding.pageNumberTextView.text = page.pageNumber

        // Saved date
        val dateColor = if (page.date.isNotEmpty()) Color.BLUE else Color.BLACK
        binding.dateTextButton.setTextColor(dateColor)
        binding.dateText.text = page.date


        //setting up seekbars
        binding.seekbarForLetterAndWord.progress = (page.letterSpace * 100).toInt()
        binding.seekbarForLineAndWord.progress = ((page.textAndLineSpace + 5f) / 30f * 100).toInt()
       // binding.ivTextEditView.letterSpacing = page.letterSpace

         // Font size and style
        when (page.fontType) {
            Typeface.NORMAL -> { }
            Typeface.BOLD -> binding.boldText.setTextColor(Color.BLUE)
            Typeface.ITALIC -> binding.italicText.setTextColor(Color.BLUE)
        }
        binding.fontSizeAutoComplete.setText("${page.fontSize}")
        binding.fontStyleAutoComplete.setText(REVERSE_FONT_STYLE_MAP[page.fontStyle])
        binding.ivImageEditView.setBackgroundColor(page.pageColor)

        // Page color and font updates
        updateFontStyle(page.fontStyle)
        updateFontType(page.fontStyle, page.fontType)
        updateLine(page.addLines, page.fontSize, page.lineColor, view)

        /** Horizontal scroll views **/

         updateUnderlineText(page.underline)
         updateDatePosition(page.dateTextViewX,page.dateTextViewY)
         INK_COLOR_MAP[page.inkColor]?.let {
            updateInkColor(page.inkColor, it)
         }
        binding.dropdownAlignment.post {
            updateTextAlignment(page.textAlignment)
            binding.dropdownAlignment.setSelection(page.textAlignment)
        }
        binding.dropdownFontSize.post {
            updateFontSize(page.fontSize)
            REVERSE_FONT_SIZE_MAP[page.fontSize]?.let { binding.dropdownFontSize.setSelection(it) }
           //val p = binding.dropdownFontSize.setSelection()
        }
    }

    private fun textAlignmentDropDown(){

        val iconAdapter = IconAdapter(requireContext(), alignmentOptions)
        binding.dropdownAlignment.adapter = iconAdapter
        binding.dropdownAlignment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    updateTextAlignment(position)
                viewModel.onEvent(PageEditEvent.UpdateTextAlignment(position))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateTextAlignment(position: Int) {
        val text = binding.ivTextEditView
        when (position) {
            0 -> {
                // Original position
                // Left alignment
                text.gravity = Gravity.START
            }
            1 -> {
                // Center alignment
                text.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            }
            2 -> {
                // Right alignment
                text.gravity = Gravity.END
            }
        }
    }

    private fun fontSizeDropDown() {
        val fontSize = resources.getStringArray(R.array.font_sizes_array).toList()
        val fontSizeAdapter = FontSizeAdapter(requireContext(), fontSize)
        binding.dropdownFontSize.adapter = fontSizeAdapter
        binding.dropdownFontSize.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                   // val fontSize = parent?.getItemAtPosition(position).toString()
                    updateFontSize(FONT_SIZES_MAP[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

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





    private fun inkColorDialog() {
        val dialogBinding = DialogInkColorBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        val inkColorAdapter = InkColorAdapter { inkColorItem ->
            updateInkColor(inkColorItem.color,inkColorItem.imageId)
            dialog.dismiss()
        }

        dialogBinding.inkColorDialogRecyclerView.adapter = inkColorAdapter
        dialogBinding.inkColorDialogRecyclerView.layoutManager = GridLayoutManager(requireContext(), 5)

        dialog.show()
    }


    private fun updateInkColor( inkColor: Int, imageId : Int,) {
        binding.ivTextEditView.setTextColor(inkColor)
        binding.demoStyleTextView.setTextColor(inkColor)
        binding.inkColorIcon.setImageResource(imageId)
        binding.pageNumberTextView.setTextColor(inkColor)
        binding.dateText.setTextColor(inkColor)
        viewModel.onEvent(PageEditEvent.UpdateInkColor(inkColor))
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
            binding.dateText.typeface = typeface
            binding.pageNumberTextView.typeface = typeface
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
            binding.dateText.typeface = typeface
            binding.pageNumberTextView.typeface = typeface
            binding.demoStyleTextView.typeface = typeface
            viewModel.onEvent(PageEditEvent.UpdateFontStyle(fontResourceId))
        }
    }


    private fun lineColorAdapter() {
        val lineColorArray= resources.getStringArray(R.array.line_color_array)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_down, lineColorArray)
        binding.lineColorAutoComplete.setAdapter(arrayAdapter)
        binding.lineColorAutoComplete.setOnItemClickListener { parent, view, position, id ->
            val lineColor = parent.getItemAtPosition(position).toString()
            updateLineColor(Constant.LINE_COLOR_MAP[lineColor])
        }
    }

    private fun updateLineColor(color: Int?) {
        if (color != null) {
            viewModel.onEvent(PageEditEvent.UpdateLineColor(color))
            updateLine(viewModel.state.value.addLines, viewModel.state.value.fontSize, color, edtPageLayoutView)
        }
    }



    private fun updateFontType(fontStyle: Int, fontType: Int) {
        val typeface = ResourcesCompat.getFont(requireContext(), fontStyle)
        binding.ivTextEditView.setTypeface(typeface, fontType)
        binding.dateText.setTypeface(typeface, fontType)
        binding.pageNumberTextView.setTypeface(typeface, fontType)
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


    private fun pickDate(callback: (dateTime: String) -> Unit) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, day)
            val dateTime = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(selectedCalendar.time)
            callback(dateTime) // call the callback function after completing the work
        }, currentYear, currentMonth, currentDay)

        datePickerDialog.show()
    }

}