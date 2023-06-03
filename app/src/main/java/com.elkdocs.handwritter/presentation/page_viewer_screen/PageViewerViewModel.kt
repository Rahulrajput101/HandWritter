package com.elkdocs.handwritter.presentation.page_viewer_screen

import android.graphics.Bitmap
import android.graphics.Typeface
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.domain.use_cases.AddNewPage
import com.elkdocs.handwritter.domain.use_cases.DeleteMyFolderWithPages
import com.elkdocs.handwritter.domain.use_cases.DeletePage
import com.elkdocs.handwritter.domain.use_cases.GetAllPages
import com.elkdocs.handwritter.util.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageViewerViewModel @Inject constructor(
    getAllPages: GetAllPages,
    private val addNewPage: AddNewPage,
    private val deletePage: DeletePage,
    val deleteMyFolderWithPages: DeleteMyFolderWithPages,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PageViewerState())
    val state : StateFlow<PageViewerState> = _state

    val folderId= savedStateHandle.get<Long>("folderId") ?: -1

    val allPages = getAllPages.invoke(folderId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val allPages2 = state.flatMapLatest { it ->
        getAllPages(it.folderId)
    }

    init {
        viewModelScope.launch {
            allPages.collect { pages ->
            }
        }
    }


    fun onEvent(event: PageViewerEvent){
        when(event){
            is PageViewerEvent.AddPage -> {
                viewModelScope.launch {
                    addNewPage(event.page)
                }
            }
            is PageViewerEvent.DeletePage -> {
                viewModelScope.launch {
                    deletePage(event.page,event.totalPages)
                }
            }

            is PageViewerEvent.DeleteFolder -> {
                viewModelScope.launch {
                    deleteMyFolderWithPages(event.folderId)
                }
            }

        }
    }

    suspend fun deleteFolder(folderId: Long){
        deleteMyFolderWithPages(folderId)
    }


    fun updateFolderId(folderId: Long) {
        _state.value = state.value.copy(folderId = folderId)
    }



}