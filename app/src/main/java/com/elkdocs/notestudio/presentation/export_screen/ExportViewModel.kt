package com.elkdocs.notestudio.presentation.export_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elkdocs.notestudio.domain.model.MyPageModel
import com.elkdocs.notestudio.domain.repository.MyRepository
import com.elkdocs.notestudio.domain.use_cases.GetAllPages
import com.elkdocs.notestudio.domain.use_cases.UpdateFolderTitle
import com.elkdocs.notestudio.presentation.folder_screen.FolderViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val updateFolderTitle: UpdateFolderTitle,
    private val getAllPages: GetAllPages,
    private val repository: MyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    val folderId = savedStateHandle.get<Long>("folderId") ?: -1
    private val _eventFlow = Channel<FolderViewModel.RenameFolderName>()
    val eventFlow = _eventFlow.receiveAsFlow()

    val allPages = getAllPages.invoke(folderId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            allPages.collect { pages ->
            }
        }
    }

    fun onEvent(event: ExportEvent) {
        when (event) {
            is ExportEvent.UpdateFolderName -> {
                viewModelScope.launch {
                    try {
                        updateFolderTitle(event.folderName, event.folderId)
                        _eventFlow.send(FolderViewModel.RenameFolderName.Success)
                        FolderViewModel.RenameFolderName.Success
                    } catch (e: Exception) {
                        _eventFlow.send(
                            FolderViewModel.RenameFolderName.Error(
                                event.folderName,
                                event.folderId
                            )
                        )
                    }
                }
            }

        }
    }

    fun getAllPagesById(folderId: Long): Flow<List<MyPageModel>> {
        return getAllPages(folderId)
            .map { pages ->
                // Perform any necessary transformations or filtering here
                pages
            }
    }


    sealed class RenameFolderName {
        object Success : RenameFolderName()
        data class Error(val folderName: String, val folderId: Long) : RenameFolderName()
    }
}