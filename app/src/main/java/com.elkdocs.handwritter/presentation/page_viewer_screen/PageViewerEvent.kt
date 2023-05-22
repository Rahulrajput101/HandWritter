package com.elkdocs.handwritter.presentation.page_viewer_screen

import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.presentation.folder_screen.FolderEvent

sealed interface PageViewerEvent{
    data class AddPage(val page: MyPageModel) : PageViewerEvent
    data class DeletePage(val page: MyPageModel) : PageViewerEvent

   // data class DeleteFolder(val folderId : Float)
}