package com.elkdocs.handwritter.presentation.export_screen

sealed interface ExportEvent {

    data class UpdateFolderName(val folderName: String, val folderId: Long) : ExportEvent


}