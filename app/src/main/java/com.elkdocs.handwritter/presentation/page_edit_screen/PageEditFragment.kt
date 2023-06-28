package com.elkdocs.handwritter.presentation.page_edit_screen

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnLayout
import androidx.core.view.drawToBitmap
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
import com.elkdocs.handwritter.databinding.DialogPageHeadingBinding
import com.elkdocs.handwritter.databinding.FragmentPageEditBinding
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.presentation.page_edit_screen.PageEditState.Companion.alignmentOptions
import com.elkdocs.handwritter.presentation.page_edit_screen.PageEditState.Companion.inputDateFormat
import com.elkdocs.handwritter.presentation.page_edit_screen.PageEditState.Companion.outputDateFormat
import com.elkdocs.handwritter.util.Constant
import com.elkdocs.handwritter.util.Constant.Ar_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.Constant.Arabic
import com.elkdocs.handwritter.util.Constant.English
import com.elkdocs.handwritter.util.Constant.FONT_SIZES_MAP
import com.elkdocs.handwritter.util.Constant.FONT_STYLES_MAP
import com.elkdocs.handwritter.util.Constant.HI_FONT_STYLES_MAP
import com.elkdocs.handwritter.util.Constant.Hindi
import com.elkdocs.handwritter.util.Constant.INK_COLOR_MAP
import com.elkdocs.handwritter.util.Constant.LANGUAGE_MAP
import com.elkdocs.handwritter.util.Constant.PHILIPINE
import com.elkdocs.handwritter.util.Constant.PH_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.Constant.REVERSE_FONT_SIZE_MAP
import com.elkdocs.handwritter.util.Constant.REVERSE_LINE_COLOR_MAP
import com.elkdocs.handwritter.util.Constant.REV_AR_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.Constant.REV_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.Constant.REV_HI_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.Constant.REV_PH_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.Constant.REV_RS_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.Constant.REV_UR_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.Constant.RS_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.Constant.Russian
import com.elkdocs.handwritter.util.Constant.Ur_FONT_STYLE_MAP
import com.elkdocs.handwritter.util.Constant.Urdu
import com.elkdocs.handwritter.util.OtherUtility.resizeBitmap
import com.elkdocs.handwritter.util.OtherUtility.setTypeface
import com.elkdocs.handwritter.util.OtherUtility.spToPx
import com.elkdocs.handwritter.util.OtherUtility.updateHeadingTextPosition
import com.elkdocs.handwritter.util.OtherUtility.updateTextPosition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_FADE
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class PageEditFragment : Fragment() {
    private lateinit var binding: FragmentPageEditBinding
    private val navArgs: PageEditFragmentArgs by navArgs()
    private val viewModel: PageEditViewModel by viewModels()
    private lateinit var pageColorAdapter: PageColorAdapter
    private var edtPageLayoutView: View? = null
    private lateinit var pageArgs : MyPageModel
    private var isLayoutFlipped: Boolean = false
    private var horizontalViewSelectedTextColor : Int = Color.BLUE

    private var offsetX: Float = 0f
    private var offsetY: Float = 0f
    private var startX: Float = 0f
    private var startY: Float = 0f

    @Inject
    @Named("theme")
    lateinit var appThemePref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment 2
        binding = FragmentPageEditBinding.inflate(layoutInflater)
        //setIconColorByTheme()

        //taking the saved details of the page for setup the ui
        val pageId = navArgs.pageId

        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                pageArgs = viewModel.getPageById(pageId)
            }
            viewModel.setPageEditState(MyPageModel.fromMyPageModel(pageArgs))
            binding.edtPageLayout.post {
                val view = binding.edtPageLayout
                edtPageLayoutView = binding.edtPageLayout
                lifecycleScope.launch {
                    withContext(Dispatchers.Main){
                        setInitialValues(pageArgs, view)
                    }
                }

                languageAdapter()
                fontStyleAdapter()
                //fontSizeAdapter()
                lineColorAdapter()
                wordSpacing()
                lineWordSpacing(view)
                dateTextTouchListener()
                headingTextTouchListener()

            }
        }

        horizontalScrollViewItems()
        pageColorAdapter()

        //This will first saved the user input in database and then it will go to the viewer screen
        binding.editForwardButton.setOnClickListener {
           savePageAndNavigateUp()
        }
        binding.editBackButton.setOnClickListener {
            savePageAndNavigateUp()
        }
        //set up of bottom sheet
        BottomSheetBehavior.from(binding.bottomSheetLayout).apply {
            peekHeight = 100
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        setupNavigation()
        return binding.root
    }


    private fun savePageAndNavigateUp() {
        binding.ivTextEditView.clearFocus()

        val resizedBitmap = resizeBitmap(binding.edtPageLayout.drawToBitmap(),viewModel.state.value.isLayoutFlipped)
        viewModel.onEvent(PageEditEvent.UpdateBitmap(resizedBitmap))

        val noteText = binding.ivTextEditView.text.toString()
        if (noteText.isNotEmpty()) {
            viewModel.onEvent(PageEditEvent.UpdateNote(noteText))
        }

        lifecycleScope.launch {
            viewModel.upsertPage()
            findNavController().navigateUp()
        }
    }

    private fun setupNavigation() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                savePageAndNavigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /** Headline Dialog and his helper functions **/
    private fun showHeadingDialog(isClicked: Boolean = false) {
        val state = viewModel.state.value
        val typeface = ResourcesCompat.getFont(requireContext(), state.fontStyle)
        val headingBinding = DialogPageHeadingBinding.inflate(layoutInflater)

        if (isClicked) {
            updateHeadingEditText(state, headingBinding.headingEditTextInput, typeface)
        } else {
            headingBinding.headingEditTextInput.setText("")
        }

        setTypeface(headingBinding.headingEditTextInput, typeface)

        MaterialAlertDialogBuilder(requireContext())
            .setView(headingBinding.root)
            .setPositiveButton("OK") { _, _ ->
                updateHeadingViewState(headingBinding)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        headingBinding.boldDialog.setOnClickListener {
            toggleTextBold(headingBinding.headingEditTextInput, typeface)
        }

        headingBinding.underlineDialog.setOnClickListener {
            toggleHeadingUnderline(headingBinding.headingEditTextInput)
        }
    }


    private fun updateHeadingEditText(state: PageEditState, editText: EditText, typeface: Typeface?) {
        val isBold = binding.headingTextView.typeface.isBold
        lifecycleScope.launch(Dispatchers.Default) {
            updateHeadingEditTextUnderline(state.headingText, state.headingUnderline, state.headingText.length) { spannableString ->
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        editText.setText(spannableString)
                        editText.setTypeface(typeface, if (isBold) Typeface.BOLD else Typeface.NORMAL)
                    }
                }
            }
        }
    }

    private fun updateHeadingEditTextUnderline(text: String, underline: Boolean, length: Int, spanString: (SpannableString) -> Unit) {
        val spannableString = SpannableString(text)
        val underlineSpans = spannableString.getSpans(0, length, UnderlineSpan::class.java)
        if (underline) {
            spannableString.setSpan(UnderlineSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            for (span in underlineSpans) {
                spannableString.removeSpan(span)
            }
        }

        spanString(spannableString)
    }

    private fun updateHeadingViewState(headingBinding: DialogPageHeadingBinding) {
        binding.addHeadingButton.setTextColor(Color.BLUE)
        val heading = headingBinding.headingEditTextInput.text.toString()
        val isBold = headingBinding.headingEditTextInput.typeface.isBold
        viewModel.onEvent(PageEditEvent.UpdateHeading(heading = heading))
        updateHeadingTextView(isBold, heading)
    }

    private fun updateHeadingTextView(isBold: Boolean, text: String) {
        updateFontTypeOfHeadingTextView(viewModel.state.value.fontStyle, if (isBold) Typeface.BOLD else Typeface.NORMAL)
        updateUnderlineHeadingTextView(viewModel.state.value.headingUnderline)
    }

    private fun toggleTextBold(editText: EditText, typeface: Typeface?) {
        val isBold = editText.typeface.isBold
        editText.setTypeface(typeface, if (isBold) Typeface.NORMAL else Typeface.BOLD)
    }

    private fun toggleHeadingUnderline(editText: EditText) {
        val headerUnderline = !viewModel.state.value.headingUnderline
        viewModel.onEvent(PageEditEvent.UpdateHeadingUnderline(headerUnderline))
        val length = editText.length()
        updateHeadingEditTextUnderline(editText.text.toString(), headerUnderline, length) { spannableString ->
            editText.setText(spannableString)
        }
    }

    private fun updateFontTypeOfHeadingTextView(fontStyle: Int, fontType: Int){
        val typeface = ResourcesCompat.getFont(requireContext(), fontStyle)
        binding.headingTextView.setTypeface(typeface,fontType)
        viewModel.onEvent(PageEditEvent.UpdateHeadingFontType(fontType))
    }

    private fun updateUnderlineHeadingTextView(underline: Boolean) {
        binding.headingTextView.text = viewModel.state.value.headingText+" "
        val spannableString = SpannableString(binding.headingTextView.text)
        val underlineSpan = spannableString.getSpans(0,binding.headingTextView.text.length-1,UnderlineSpan::class.java)
        if (underline) {
            spannableString.setSpan(UnderlineSpan(), 0 , binding.headingTextView.text.length-1 ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            //binding.headingTextView.paintFlags =binding.headingTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        } else {
            for(span in underlineSpan ){
                spannableString.removeSpan(span)
            }
           // binding.headingTextView.paintFlags = binding.headingTextView.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
        }
        binding.headingTextView.setText(spannableString)

//        val spannableString = SpannableString(text)
//        val underlineSpans = spannableString.getSpans(0, length, UnderlineSpan::class.java)
//        if (underline) {
//            spannableString.setSpan(UnderlineSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        } else {
//            for (span in underlineSpans) {
//                spannableString.removeSpan(span)
//            }
//        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun headingTextTouchListener() {

        val gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener(){
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                showHeadingDialog(true)
                return true
            }

        })
        binding.headingTextView.setOnTouchListener { v, event ->
            val parentView = binding.headlineParentConstraint
            val rotationAngle = if (viewModel.state.value.isLayoutFlipped) 180f else 0f

            val screenX = event.rawX
            val screenY = event.rawY

            val viewX = v.x
            val viewY = v.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    offsetX = screenX - viewX
                    offsetY = screenY - viewY
                    startX = viewX
                    startY = viewY
                }

                MotionEvent.ACTION_MOVE -> {
                    val newX = screenX - offsetX
                    val newY = screenY - offsetY

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
                    viewModel.onEvent(PageEditEvent.UpdateHeadingTextPosition(v.x, v.y))
                }
            }

            gestureDetector.onTouchEvent(event)
            true
        }
    }

    /**  End  **/





    private fun pageNumberDialog() {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_page_number, null)
            MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("OK") { _, _ ->
                    val pageNumberEditText = dialogView.findViewById<EditText>(R.id.editPageNumber)
                    val pageNumber = pageNumberEditText.text.toString()
                    if(pageNumber.isNotEmpty()){
                        binding.pageNumberTextView.text = pageNumber
                        binding.addPageNuumberButton.setTextColor(horizontalViewSelectedTextColor)
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
            val parentView = binding.dateTextConstraintLayout
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

        binding.addHeadingButton.setOnClickListener {

               updateHeading(binding.headingTextView.text.toString())
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

        if(page.isLayoutFlipped){
            flipLayout(true)
            binding.ivTextEditView.gravity = Gravity.END
            binding.demoStyleTextView.gravity = Gravity.END
        }else{
            flipLayout(false)
            binding.ivTextEditView.gravity = Gravity.START
            binding.demoStyleTextView.gravity = Gravity.START
        }

        binding.headingTextView.apply {
            text = page.headingText
            setTextColor(page.inkColor)
            letterSpacing = page.letterSpace
        }

        //toggling heading button color
        val headingButtonColor = if (page.headingText.isNotEmpty()) horizontalViewSelectedTextColor else Color.BLACK
        binding.addHeadingButton.setTextColor(headingButtonColor)
        // Page number
        val pageNumberColor = if (page.pageNumber.isNotEmpty()) horizontalViewSelectedTextColor else Color.BLACK
        binding.addPageNuumberButton.setTextColor(pageNumberColor)
        binding.pageNumberTextView.text = page.pageNumber

        // Saved date
        val dateColor = if (page.date.isNotEmpty()) horizontalViewSelectedTextColor else Color.BLACK
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


       val selectedFont = when(page.language) {
            English -> REV_FONT_STYLE_MAP[page.fontStyle]
            Hindi -> REV_HI_FONT_STYLE_MAP[page.fontStyle]
            PHILIPINE -> REV_PH_FONT_STYLE_MAP[page.fontStyle]
            Arabic -> REV_AR_FONT_STYLE_MAP[page.fontStyle]
            Russian -> REV_RS_FONT_STYLE_MAP[page.fontStyle]
            Urdu -> REV_UR_FONT_STYLE_MAP[page.fontStyle]
            else -> REV_FONT_STYLE_MAP[page.fontStyle]
        }


        binding.fontStyleAutoComplete.setText(selectedFont)
        binding.lineColorAutoComplete.setText(REVERSE_LINE_COLOR_MAP[page.lineColor])
        binding.languageAutoComplete.setText(page.language)
        viewModel.onEvent(PageEditEvent.UpdateLanguage(page.language))
        binding.ivImageEditView.setBackgroundColor(page.pageColor)

        // Page color and font updates
        updateFontStyle(page.fontStyle)
        updateFontType(page.fontStyle, page.fontType)
        updateLine(page.addLines, page.fontSize, page.lineColor, view)

        /** Horizontal scroll views **/

        updateUnderlineHeadingTextView(page.headingUnderline)
        updateFontTypeOfHeadingTextView(page.fontStyle,page.headingFontType)
        //For date
        updateTextPosition(binding.dateText,page.dateTextViewX,page.dateTextViewY)
        //For Heading
        lifecycleScope.launch {
            binding.headlineParentConstraint.post {
                updateHeadingTextPosition(binding.headingTextView,page.headingTextViewX,page.headingTextViewY)
            }
        }

        //updateDatePosition(page.dateTextViewX,page.dateTextViewY)
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
        }
    }


    private fun updateDate(addDate : String){
        if(addDate.isEmpty()){
            pickDate {
                val date = inputDateFormat.parse(it)
                date?.let {
                    val formattedDate = outputDateFormat.format(date)
                    binding.dateTextButton.setTextColor(horizontalViewSelectedTextColor)
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
            binding.dateText.text = ""
            viewModel.onEvent(PageEditEvent.UpdateDateTextPosition(0f,0f))
            viewModel.onEvent(PageEditEvent.UpdateDate(""))
        }
    }

    private fun updateHeading(headingText : String){
        if(headingText.isBlank()){
            showHeadingDialog()
            val x = binding.headingTextView.x
            val y = binding.headingTextView.y
            viewModel.onEvent(PageEditEvent.UpdateHeadingTextPosition(x,y))
        }else{
            binding.headingTextView.setText("")
            binding.addHeadingButton.setTextColor(Color.BLACK)
            viewModel.onEvent(PageEditEvent.UpdateHeadingUnderline(false))
            viewModel.onEvent(PageEditEvent.UpdateHeadingTextPosition(0f,0f))
            viewModel.onEvent(PageEditEvent.UpdateHeading(""))
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


    private fun updateUnderlineText(underline: Boolean) {
            if (underline) {
                // Add underline
                binding.underlineText.setTextColor(horizontalViewSelectedTextColor)
                binding.ivTextEditView.paintFlags =binding.ivTextEditView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            } else {
                // Remove underline
                binding.underlineText.setTextColor(Color.BLACK)
                binding.ivTextEditView.paintFlags = binding.ivTextEditView.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            }

        viewModel.onEvent(PageEditEvent.UpdateUnderLine(underline))
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
                    binding.boldText.setTextColor(horizontalViewSelectedTextColor)
                } else {
                    binding.boldText.setTextColor(Color.BLACK)
                }
            }

            R.id.italic_text -> {
                binding.boldText.setTextColor(Color.BLACK)
                updateFontType(viewModel.state.value.fontStyle, if (isItalic) Typeface.NORMAL else Typeface.ITALIC)
                if (!isItalic) {
                    binding.italicText.setTextColor(horizontalViewSelectedTextColor)

                } else {
                    binding.italicText.setTextColor(Color.BLACK)
                }
            }
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
//        when (position) {
//            0 -> {
//                // Original position
//                // Left alignment
//                text.gravity = Gravity.START
//            }
//            1 -> {
//                // Center alignment
//                text.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
//            }
//            2 -> {
//                // Right alignment
//                text.gravity = Gravity.END
//            }
//        }
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
            val view = edtPageLayoutView ?: return
            updateLine(viewModel.state.value.addLines, fontSizeValue, viewModel.state.value.lineColor, view )
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
        binding.headingTextView.setTextColor(inkColor)
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
        val fontStyles = getFontListForLanguage(viewModel.state.value.language)
        val fontMap = getFontMapForLanguage(viewModel.state.value.language)
        val arrayAdapter = FontStyleAdapter(requireContext(),R.layout.item_drop_down,fontStyles ,fontMap)
        binding.fontStyleAutoComplete.setAdapter(arrayAdapter)
        binding.fontStyleAutoComplete.setOnItemClickListener { parent, view, position, id ->
            val fontStyle = parent.getItemAtPosition(position).toString()
            binding.boldText.setTextColor(Color.BLACK)
            binding.italicText.setTextColor(Color.BLACK)

            when (viewModel.state.value.language) {
                English -> updateFontStyle(FONT_STYLES_MAP[fontStyle])
                Hindi -> updateFontStyle(HI_FONT_STYLES_MAP[fontStyle])
                PHILIPINE -> updateFontStyle(PH_FONT_STYLE_MAP[fontStyle])
                Arabic -> updateFontStyle(Ar_FONT_STYLE_MAP[fontStyle])
                Russian -> updateFontStyle(RS_FONT_STYLE_MAP[fontStyle])
                Urdu -> updateFontStyle(Ur_FONT_STYLE_MAP[fontStyle])
                else -> updateFontStyle(FONT_STYLES_MAP[fontStyle])
            }
        }
    }

    private fun getFontListForLanguage(language: String): Array<String> {
        return when (language) {
            English -> resources.getStringArray(R.array.font_styles_array)
            Hindi -> resources.getStringArray(R.array.hi_styles_array)
            PHILIPINE -> resources.getStringArray(R.array.ph_styles_array)
            Arabic -> resources.getStringArray(R.array.ar_styles_array)
            Russian -> resources.getStringArray(R.array.rs_styles_array)
            Urdu -> resources.getStringArray(R.array.ur_styles_array)
            else -> resources.getStringArray(R.array.font_styles_array)
        }
    }

    private fun getFontMapForLanguage(language: String): Map<String, Int> {
        return when (language) {
            English -> FONT_STYLES_MAP
            Hindi -> HI_FONT_STYLES_MAP
            PHILIPINE -> PH_FONT_STYLE_MAP
            Arabic -> Ar_FONT_STYLE_MAP
            Russian -> RS_FONT_STYLE_MAP
            Urdu -> Ur_FONT_STYLE_MAP
            else -> FONT_STYLES_MAP
        }
    }


    private fun updateFontStyle(fontResourceId: Int?) {
        if (fontResourceId != null) {
            val typeface = ResourcesCompat.getFont(requireContext(), fontResourceId)
            binding.ivTextEditView.typeface = typeface
            binding.dateText.typeface = typeface
            binding.pageNumberTextView.typeface = typeface
            binding.demoStyleTextView.typeface = typeface
            binding.headingTextView.typeface = typeface
            binding.fontStyleAutoComplete.typeface = typeface
            viewModel.onEvent(PageEditEvent.UpdateFontStyle(fontResourceId))
        }
    }

//    private fun languageAdapter() {
//        val language = resources.getStringArray(R.array.languages_array)
//        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_down, language)
//        binding.languageAutoComplete.setAdapter(arrayAdapter)
//        binding.languageAutoComplete.setOnItemClickListener { parent, view, position, id ->
//            val fontStyle = parent.getItemAtPosition(position).toString()
//            binding.boldText.setTextColor(Color.BLACK)
//            binding.italicText.setTextColor(Color.BLACK)
//            viewModel.onEvent(PageEditEvent.UpdateLanguage(fontStyle))
//            updateLanguage(LANGUAGE_MAP[fontStyle])
//
//            //list will change according to the language
//            val getFontStyleList = getFontListForLanguage(viewModel.state.value.language)
//            binding.fontStyleAutoComplete.setText(getFontStyleList[0])
//
//            fontStyleAdapter()
//        }
//    }
//

    private fun languageAdapter() {
        val languageArray = resources.getStringArray(R.array.languages_array)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_down, languageArray)
        binding.languageAutoComplete.setAdapter(arrayAdapter)

        binding.languageAutoComplete.setOnItemClickListener { parent, view, position, id ->
            val selectedLanguage = languageArray[position]
            binding.boldText.setTextColor(Color.BLACK)
            binding.italicText.setTextColor(Color.BLACK)
            viewModel.onEvent(PageEditEvent.UpdateLanguage(selectedLanguage))
            updateLanguage(LANGUAGE_MAP[selectedLanguage])

            // Update the font style list based on the selected language
            val fontStyleList = getFontListForLanguage(selectedLanguage)
            binding.fontStyleAutoComplete.setText(fontStyleList[0])
            fontStyleAdapter()
        }
    }

    private fun updateLanguage(fontResourceId: Int?) {
        if (fontResourceId != null) {
            val typeface = ResourcesCompat.getFont(requireContext(), fontResourceId)
            binding.ivTextEditView.typeface = typeface
            if(fontResourceId == R.font.scheherazade_ar || fontResourceId == R.font.sarmady_ar_ur){
                // Flip the layout horizontally if the chosen language is Arabic
                    flipLayout(true)
                    binding.ivTextEditView.gravity = Gravity.END
                    binding.demoStyleTextView.gravity = Gravity.END
                    viewModel.onEvent(PageEditEvent.UpdateLayoutFlipped(true))

            } else {
                // Restore the original layout if the chosen language is not Arabic
                    flipLayout(false)
                    binding.ivTextEditView.gravity = Gravity.START
                     binding.demoStyleTextView.gravity = Gravity.START
                    viewModel.onEvent(PageEditEvent.UpdateLayoutFlipped(false))

            }
            binding.dateText.typeface = typeface
            binding.pageNumberTextView.typeface = typeface
            binding.demoStyleTextView.typeface = typeface
            binding.fontStyleAutoComplete.typeface = typeface
            viewModel.onEvent(PageEditEvent.UpdateFontStyle(fontResourceId))
        }
    }

    private fun flipLayout(flip : Boolean) {
        val rotationAngle = if (flip) 180f else 0f
        binding.edtPageLayout.rotationY = rotationAngle
        binding.ivTextEditView.rotationY = rotationAngle
        binding.headlineParentConstraint.rotationY = rotationAngle
        binding.dateTextConstraintLayout.rotationY =rotationAngle
        binding.pageNumberTextView.rotationY = rotationAngle
        isLayoutFlipped = !isLayoutFlipped
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
        val view = edtPageLayoutView ?: return
        if (color != null) {
            if(color == -1){
                viewModel.onEvent(PageEditEvent.UpdateLineColor(color))
                updateLine(hasLine = false,viewModel.state.value.fontSize, color, view)
            }else{
                viewModel.onEvent(PageEditEvent.UpdateLineColor(color))
                updateLine(hasLine = true, viewModel.state.value.fontSize, color, view)
            }
        }
    }



    private fun updateFontType(fontStyle: Int, fontType: Int) {
        val typeface = ResourcesCompat.getFont(requireContext(), fontStyle)
        binding.ivTextEditView.setTypeface(typeface, fontType)
        binding.dateText.setTypeface(typeface, fontType)
        binding.pageNumberTextView.setTypeface(typeface, fontType)
        binding.headingTextView.setTypeface(typeface,fontType)
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
                    // Clear the drawn lines
                    binding.ivImageEditView.setImageDrawable(null)
                    binding.ivImageDemoView.setImageDrawable(null)
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
        linePaint.isAntiAlias = true
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
        linePaint.isAntiAlias = true
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
                binding.headingTextView.letterSpacing = spacingValue
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

    private fun setIconColorByTheme() {

//        val colorResId = when (appThemePref.getInt(Constant.APP_THEME_PREF, R.style.AppTheme_teal)) {
//            R.style.AppTheme -> R.color.md_theme_light_tertiaryContainer
//            R.style.AppTheme_Green -> R.color.md_theme_light_tertiaryContainer2
//            R.style.AppTheme_pink -> R.color.md_theme_light_tertiaryContainer3
//            R.style.AppTheme_teal -> R.color.md_theme_light_tertiaryContainer4
//            R.style.AppTheme_purple -> R.color.md_theme_light_tertiaryContainer5
//            else -> R.color.md_theme_light_tertiaryContainer4
//        }

        val colorResId2 = when (appThemePref.getInt(Constant.APP_THEME_PREF, R.style.AppTheme_teal)) {
            R.style.AppTheme -> R.color.md_theme_light_surfaceTint
            R.style.AppTheme_Green -> R.color.md_theme_light_surfaceTint2
            R.style.AppTheme_pink -> R.color.md_theme_light_surfaceTint3
            R.style.AppTheme_teal -> R.color.md_theme_light_surfaceTint4
            R.style.AppTheme_purple -> R.color.md_theme_light_surfaceTint5
            else -> R.color.md_theme_light_surfaceTint2
        }

        //val color = ContextCompat.getColor(requireContext(), colorResId)
        val color2 = ContextCompat.getColor(requireContext(), colorResId2)
        horizontalViewSelectedTextColor = color2
    }

}