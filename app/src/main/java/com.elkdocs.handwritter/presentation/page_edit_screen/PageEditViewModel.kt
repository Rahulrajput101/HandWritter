package com.elkdocs.handwritter.presentation.page_edit_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.domain.use_cases.AddNewPage
import com.elkdocs.handwritter.domain.use_cases.DrawLine
import com.elkdocs.handwritter.domain.use_cases.GetAllPages
import com.elkdocs.handwritter.util.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageEditViewModel @Inject constructor(
    private val addNewPage: AddNewPage,
    private val drawLine: DrawLine,
    getAllPages: GetAllPages,
) : ViewModel() {

    private val _state = MutableStateFlow(PageEditState())
    val state : StateFlow<PageEditState> = _state


    fun onEvent(event: PageEditEvent){
        when(event){

            is PageEditEvent.UpdateFontStyle -> {
                _state.value = _state.value.copy(fontStyle = event.fontStyle)
            }

            is PageEditEvent.UpdateFontSize -> {
                _state.value = _state.value.copy(fontSize = event.fontSize)
            }

            is PageEditEvent.UpdateAddLine -> {
                _state.value = _state.value.copy(addLines = event.addLine)
            }

            is PageEditEvent.UpdateLineColor -> {
                _state.value = _state.value.copy(lineColor = event.lineColor)
            }

            is PageEditEvent.UpdateInkColor -> {
                _state.value = _state.value.copy(inkColor = event.inkColor)
            }

            is PageEditEvent.UpdateNote -> {
                _state.value = _state.value.copy(notesText = event.text)
            }

            is PageEditEvent.UpdateFontType -> {
                _state.value =_state.value.copy(fontType = event.fontType)
            }

            is PageEditEvent.UpdatePageColor -> {
                _state.value = _state.value.copy(pageColor = event.pageColor)
            }

            is PageEditEvent.UpdatePage -> {
                viewModelScope.launch {
                    addNewPage(
                        MyPageModel(
                            pageId = state.value.pageId,
                            folderId = state.value.folderId,
                            pageNumber = state.value.pageNumber,
                            uriIndex = state.value.uriIndex,
                            notesText = state.value.notesText,
                            textAlignment = state.value.textAlignment,
                            fontStyle = state.value.fontStyle,
                            fontType =state.value.fontType,
                            letterSpace = state.value.letterSpace,
                            fontSize = state.value.fontSize,
                            textAndLineSpace = state.value.textAndLineSpace,
                            addLines =state.value.addLines,
                            lineColor = state.value.lineColor,
                            inkColor = state.value.inkColor,
                            pageColor = state.value.pageColor,
                            underline = state.value.underline,
                            bitmap = state.value.pageBitmap,
                            date = state.value.date,
                            dateTextViewX= state.value.dateTextViewX,
                            dateTextViewY= state.value.dateTextViewY
                        )
                    )
                }
            }

            is PageEditEvent.DrawLine -> {
                drawLine(
                    canvas = event.canvas,
                    fontSize = state.value.fontSize,
                    lineColor = Constant.BLUE_LINE_COLOR
                )
            }

            is PageEditEvent.UpdateLetterSpacing -> {
                _state.value = _state.value.copy(letterSpace = event.letterSpacing)
            }

            is PageEditEvent.UpdateBitmap -> {
                _state.value = _state.value.copy(pageBitmap = event.bitmap)
            }

            is PageEditEvent.UpdateTextAndLineSpacing ->{
                _state.value = _state.value.copy(textAndLineSpace = event.textAndLineSpacing)
            }

            is PageEditEvent.UpdateUnderLine -> {
                _state.value = _state.value.copy(underline = event.underLine)
            }

            is PageEditEvent.UpdateDate -> {
                _state.value = _state.value.copy(date = event.date)
            }

            is PageEditEvent.UpdateDateTextPosition -> {
                _state.value = _state.value.copy(
                    dateTextViewX = event.dateTextViewX,
                    dateTextViewY = event.dateTextViewY
                    )
            }

            is PageEditEvent.UpdateTextAlignment -> {
                _state.value = _state.value.copy(textAlignment = event.alignment)
            }

            is PageEditEvent.UpdatePageNumber -> {
                _state.value = _state.value.copy(pageNumber = event.pageNumber)
            }
            else -> {}
        }
    }

    fun setPageEditState(pageEditState: PageEditState){
        _state.value = pageEditState
    }

    suspend fun upsertPage(){
        addNewPage(
            MyPageModel(
                pageId = state.value.pageId,
                folderId = state.value.folderId,
                pageNumber = state.value.pageNumber,
                uriIndex = state.value.uriIndex,
                notesText = state.value.notesText,
                textAlignment = state.value.textAlignment,
                fontStyle = state.value.fontStyle,
                fontType =state.value.fontType,
                letterSpace = state.value.letterSpace,
                fontSize = state.value.fontSize,
                textAndLineSpace = state.value.textAndLineSpace,
                addLines =state.value.addLines,
                lineColor = state.value.lineColor,
                inkColor = state.value.inkColor,
                pageColor = state.value.pageColor,
                underline = state.value.underline,
                bitmap = state.value.pageBitmap,
                date = state.value.date,
                dateTextViewX= state.value.dateTextViewX,
                dateTextViewY= state.value.dateTextViewY
            )
        )
    }
}