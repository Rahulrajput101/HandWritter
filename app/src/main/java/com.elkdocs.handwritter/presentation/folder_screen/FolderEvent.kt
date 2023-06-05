package com.elkdocs.handwritter.presentation.folder_screen

import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel

sealed interface FolderEvent{
    data class AddFolder(val myFolderModel: MyFolderModel) : FolderEvent
    data class DeleteFolderWithPages(val folderId : Long) : FolderEvent

    data class UpdateFolderName(val folderName: String, val folderId: Long) : FolderEvent



}