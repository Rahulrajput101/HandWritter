package com.elkdocs.notestudio.presentation.page_viewer_screen

import android.graphics.Bitmap
import com.elkdocs.notestudio.domain.model.MyPageModel

sealed interface PageViewerEvent{
    data class AddPage(val page: MyPageModel) : PageViewerEvent
    data class DeletePage(val page: MyPageModel, val totalPages : Int) : PageViewerEvent

    data class DeleteFolder(val folderId : Long) : PageViewerEvent

    data class IncreasePageCount(val folderId: Long) : PageViewerEvent
    data class DecreasePageCount(val folderId: Long, val totalPages: Int) : PageViewerEvent

    data class UpdateFolderIcon(val folderId: Long,val folderIcon : Bitmap?) : PageViewerEvent

}