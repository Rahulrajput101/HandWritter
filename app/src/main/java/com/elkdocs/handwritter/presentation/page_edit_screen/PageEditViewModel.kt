package com.elkdocs.handwritter.presentation.page_edit_screen

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.domain.use_cases.AddNewPage
import com.elkdocs.handwritter.domain.use_cases.DrawLine
import com.elkdocs.handwritter.domain.use_cases.GetAllPages
import com.elkdocs.handwritter.presentation.page_viewer_screen.PageViewerState
import com.elkdocs.handwritter.util.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
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

//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    val allPages = state.flatMapLatest { it ->
//        getAllPages(it.folderId!!)
//    }

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
                            uriIndex = state.value.uriIndex,
                            notesText = state.value.notesText,
                            fontStyle = state.value.fontStyle,
                            fontType =state.value.fontType,
                            charSpace = state.value.charSpace,
                            fontSize = state.value.fontSize,
                            wordSpace = state.value.wordSpace,
                            addLines =state.value.addLines,
                            lineColor = state.value.lineColor,
                            pageColor = state.value.pageColor
                        )
                    )
                }
            }

            is PageEditEvent.DrawLine -> {
                drawLine(
                    canvas = event.canvas,
                    fontSize = state.value.fontSize,
                    lineColor = Constant.PURPLE_LINE_COLOR
                )
            }


        }
    }

    fun setPageEditState(pageEditState: PageEditState){
        _state.value = pageEditState
    }


}