package com.elkdocs.handwritter.presentation.folder_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.domain.repository.MyRepository
import com.elkdocs.handwritter.domain.use_cases.AddNewFolder
import com.elkdocs.handwritter.domain.use_cases.DeleteMyFolderWithPages
import com.elkdocs.handwritter.domain.use_cases.GetAllFolders
import com.elkdocs.handwritter.domain.use_cases.GetAllPages
import com.elkdocs.handwritter.domain.use_cases.UpdateFolderTitle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    getAllFolders: GetAllFolders,
    private val getAllPages: GetAllPages,
    val deleteMyFolderWithPages: DeleteMyFolderWithPages,
    private val updateFolderTitle: UpdateFolderTitle,
    private val repository: MyRepository,
    private val addNewFolder: AddNewFolder
) : ViewModel() {

    private val _state = MutableStateFlow(FolderState())
    val state: StateFlow<FolderState> = _state

    private val _searchResults = MutableStateFlow<List<MyFolderModel>>(emptyList())
    val searchResults: StateFlow<List<MyFolderModel>> = _searchResults

    private val _eventFlow = Channel<RenameFolderName>()
    val eventFlow = _eventFlow.receiveAsFlow()

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
                    deleteMyFolderWithPages(event.folderId)
                }
            }

            is FolderEvent.UpdateFolderName -> {
                viewModelScope.launch {
                    try{
                        updateFolderTitle(event.folderName,event.folderId)
                        _eventFlow.send(RenameFolderName.Success)
                        RenameFolderName.Success
                    }catch (e : Exception){
                        _eventFlow.send(RenameFolderName.Error(event.folderName,event.folderId))
                    }
                }
            }
        }
    }

    fun searchFolderByName(folderName: String) : Flow<List<MyFolderModel>> {
        return repository.searchFolderByName(folderName)

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
        data class Error(val folderName : String ,val folderId : Long) : RenameFolderName()
    }


}