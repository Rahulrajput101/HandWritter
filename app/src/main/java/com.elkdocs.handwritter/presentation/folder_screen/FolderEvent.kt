package com.elkdocs.handwritter.presentation.folder_screen

import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel

sealed interface FolderEvent{
    data class AddFolder(val myFolderModel: MyFolderModel) : FolderEvent
    data class DeleteFolderWithPages(val FolderModel : MyFolderModel) : FolderEvent


}