package com.elkdocs.handwritter.domain.use_cases

import android.util.Log
import android.widget.Toast
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.domain.repository.MyFolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPages @Inject constructor(
    private val repository: MyFolderRepository
) {

    operator fun invoke(folderId : Long) : Flow<List<MyPageModel>> {
        Log.d("TAG","$folderId r")
        return repository.getAllPages(folderId)
    }
}