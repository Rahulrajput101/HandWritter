package com.elkdocs.handwritter.domain.use_cases

import android.util.Log
import com.elkdocs.handwritter.domain.repository.MyFolderRepository
import javax.inject.Inject

class UpdateFolderTitle @Inject constructor(
    private val repository: MyFolderRepository
) {
    suspend operator fun invoke(folderName : String , folderId : Long){
        val existingFolder = repository.getMyFolderByName(folderName)
        if(existingFolder != null && existingFolder.folderId != folderId){
                // A note with the new title already exists
                throw Exception("A note with the same title already exists.")
        }else{
            repository.updateFolderName(folderName,folderId)
        }
    }
}