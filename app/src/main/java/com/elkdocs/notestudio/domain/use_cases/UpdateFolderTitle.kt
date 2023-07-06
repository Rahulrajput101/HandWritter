package com.elkdocs.notestudio.domain.use_cases

import com.elkdocs.notestudio.domain.repository.MyRepository
import javax.inject.Inject

class UpdateFolderTitle @Inject constructor(
    private val repository: MyRepository
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