package com.elkdocs.notestudio.domain.use_cases

import com.elkdocs.notestudio.domain.model.MyFolderModel
import com.elkdocs.notestudio.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFolders @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke() : Flow<List<MyFolderModel>> = repository.getAllFolder()

}