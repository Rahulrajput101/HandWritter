package com.elkdocs.notestudio.presentation.folder_screen

import com.elkdocs.notestudio.domain.model.MyFolderModel

sealed interface FolderEvent{
    data class AddFolder(val myFolderModel: MyFolderModel) : FolderEvent
    data class DeleteFolderWithPages(val folderId : Long) : FolderEvent

    data class UpdateFolderName(val folderName: String, val folderId: Long) : FolderEvent



}