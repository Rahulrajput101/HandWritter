package com.elkdocs.notestudio.domain.use_cases

import android.graphics.Bitmap
import com.elkdocs.notestudio.domain.repository.MyRepository
import javax.inject.Inject

class UpdateFolderIcon @Inject constructor(
private val repository: MyRepository
) {
    suspend operator fun invoke( folderId : Long, folderIcon : Bitmap?){
        if(folderIcon != null){
            repository.updateFolderIcon(folderId,folderIcon)
        }
    }
}