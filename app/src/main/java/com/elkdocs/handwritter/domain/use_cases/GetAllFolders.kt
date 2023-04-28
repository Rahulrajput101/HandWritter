package com.elkdocs.handwritter.domain.use_cases

import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.repository.MyFolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFolders @Inject constructor(
    private val repository: MyFolderRepository
) {
    operator fun invoke() : Flow<List<MyFolderModel>> = repository.getAllFolder()

}