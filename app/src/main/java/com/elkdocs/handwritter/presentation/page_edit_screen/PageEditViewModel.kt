package com.elkdocs.handwritter.presentation.page_edit_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elkdocs.handwritter.domain.use_cases.GetAllPages
import com.elkdocs.handwritter.presentation.page_viewer_screen.PageViewerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class PageEditViewModel @Inject constructor(
    getAllPages: GetAllPages,
) : ViewModel() {

    private val _state = MutableStateFlow(PageEditState())
    val state : StateFlow<PageEditState> = _state

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    var fontStyleItem = ""
        private set
    var fontSizeItem = ""
        private set
    var addLineItem = ""
        private set
    var lineColorItem = ""
        private set

//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    val allPages = state.flatMapLatest { it ->
//        getAllPages(it.folderId!!)
//    }

    fun updateText(text : String){
        _text.value = text
    }

    fun setFontStyleItem(item: String) {
        fontStyleItem = item
    }

    fun setFontSizeItem(item: String) {
        fontSizeItem = item
    }

    fun setAddLineItem(item: String) {
        addLineItem = item
    }

    fun setLineColorItem(item: String) {
        lineColorItem = item
    }

    fun setPageEditState(pageEditState: PageEditState){
        _state.value = pageEditState
    }


}