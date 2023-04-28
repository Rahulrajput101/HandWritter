package com.elkdocs.handwritter.presentation.folder_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.use_cases.AddNewFolder
import com.elkdocs.handwritter.domain.use_cases.GetAllFolders
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    getAllFolders: GetAllFolders,
    private val addNewFolder: AddNewFolder
) : ViewModel() {
    
    private val _state = MutableStateFlow(FolderState())
    val state: StateFlow<FolderState> = _state

    val allFolders = getAllFolders().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun onEvent(event : FolderEvent,callback : (Long?) -> Unit ) {
        when(event){
            is FolderEvent.AddFolder -> {
                viewModelScope.launch {
                    val  id =addNewFolder(event.myFolderModel)
                   callback(id)
                }
            }

            is FolderEvent.DeleteFolder -> {
                viewModelScope.launch {
                    TODO()
                }
            }
        }

    }

}