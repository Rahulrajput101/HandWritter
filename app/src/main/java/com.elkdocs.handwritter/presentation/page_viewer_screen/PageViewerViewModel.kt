package com.elkdocs.handwritter.presentation.page_viewer_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.domain.use_cases.AddNewPage
import com.elkdocs.handwritter.domain.use_cases.GetAllPages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageViewerViewModel @Inject constructor(
    getAllPages: GetAllPages,
    private val addNewPage: AddNewPage
) : ViewModel() {

    private val _state = MutableStateFlow(PageViewerState())
    val state : StateFlow<PageViewerState> = _state

    //val allPages = getAllPages(state.value.folderId)
    @OptIn(ExperimentalCoroutinesApi::class)
    val allPages = state.flatMapLatest { it ->
        getAllPages(it.folderId)
    }


    fun onEvent(event: PageViewerEvent){
        when(event){
            is PageViewerEvent.AddPage -> {
                viewModelScope.launch {
                  addNewPage(event.page)
                }
            }
            is PageViewerEvent.DeletePage -> TODO()
        }
    }

    fun updateFolderId(folderId: Long) {
        _state.value = state.value.copy(folderId = folderId)
    }

}