package com.elkdocs.handwritter.presentation.folder_screen

import com.elkdocs.handwritter.domain.model.MyFolderModel

sealed interface FolderEvent{
    data class AddFolder(val myFolderModel: MyFolderModel) : FolderEvent
     data class DeleteFolder(val FolderModel : MyFolderModel) : FolderEvent

}