package com.elkdocs.handwritter.presentation.folder_screen

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.domain.use_cases.AddNewFolder
import com.elkdocs.handwritter.domain.use_cases.DeleteMyFolderWithPages
import com.elkdocs.handwritter.domain.use_cases.GetAllFolders
import com.elkdocs.handwritter.domain.use_cases.GetAllPages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    getAllFolders: GetAllFolders,
    private val getAllPages: GetAllPages,
    val deleteMyFolderWithPages: DeleteMyFolderWithPages,
    private val addNewFolder: AddNewFolder
) : ViewModel() {

    private val _state = MutableStateFlow(FolderState())
    val state: StateFlow<FolderState> = _state

    val allFolders = getAllFolders().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())



    fun onEvent(event: FolderEvent, callback: (Long,String) -> Unit) {
        when(event){
            is FolderEvent.AddFolder -> {
                viewModelScope.launch {
                    val  id = addNewFolder(event.myFolderModel)
                    callback(id,event.myFolderModel.folderName)
                }
            }

            is FolderEvent.DeleteFolderWithPages -> {
                viewModelScope.launch {
                    deleteMyFolderWithPages(event.FolderModel.folderId!!)
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


}