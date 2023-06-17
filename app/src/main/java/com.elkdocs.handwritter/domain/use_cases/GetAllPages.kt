package com.elkdocs.handwritter.domain.use_cases

import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPages @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(folderId : Long) : Flow<List<MyPageModel>> {

        return repository.getAllPages(folderId)
    }
}