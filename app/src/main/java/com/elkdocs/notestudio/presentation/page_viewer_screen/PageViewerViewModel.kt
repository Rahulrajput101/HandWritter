package com.elkdocs.notestudio.presentation.page_viewer_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elkdocs.notestudio.domain.model.MyPageModel
import com.elkdocs.notestudio.domain.use_cases.AddNewPage
import com.elkdocs.notestudio.domain.use_cases.DeleteMyFolderWithPages
import com.elkdocs.notestudio.domain.use_cases.DeletePage
import com.elkdocs.notestudio.domain.use_cases.GetAllPages
import com.elkdocs.notestudio.domain.use_cases.UpdateFolderIcon
import com.elkdocs.notestudio.domain.use_cases.UpdatePageCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageViewerViewModel @Inject constructor(
    getAllPages: GetAllPages,
    private val addNewPage: AddNewPage,
    private val updateFolderIcon : UpdateFolderIcon,
    private val deletePage: DeletePage,
    private val updatePageCount: UpdatePageCount,
    val deleteMyFolderWithPages: DeleteMyFolderWithPages,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PageViewerState())
    val state : StateFlow<PageViewerState> = _state

    val folderId= savedStateHandle.get<Long>("folderId") ?: -1

    val allPages = getAllPages.invoke(folderId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val allPages2 = state.flatMapLatest {
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
                    deletePage(event.page)
                }
            }

            is PageViewerEvent.DeleteFolder -> {
                viewModelScope.launch {
                    deleteMyFolderWithPages(event.folderId)
                }
            }

            is PageViewerEvent.IncreasePageCount -> {
                viewModelScope.launch() {
                    updatePageCount(event.folderId)
                }
            }

            is PageViewerEvent.DecreasePageCount -> {
                viewModelScope.launch {
                    updatePageCount(event.folderId,event.totalPages)
                }
            }

            is PageViewerEvent.UpdateFolderIcon -> {
                viewModelScope.launch {
                    updateFolderIcon(event.folderId,event.folderIcon)
                }
            }
        }
    }

    suspend fun deleteFolder(folderId: Long){
        deleteMyFolderWithPages(folderId)
    }

    suspend fun deleteAll(page : MyPageModel){
        deletePage(page)
    }


    fun updateFolderId(folderId: Long) {
        _state.value = state.value.copy(folderId = folderId)
    }





}