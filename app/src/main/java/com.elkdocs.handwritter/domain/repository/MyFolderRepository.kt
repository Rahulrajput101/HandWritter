package com.elkdocs.handwritter.domain.repository


import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel
import kotlinx.coroutines.flow.Flow

interface MyFolderRepository {

    suspend fun addMyFolder(myFolderModel: MyFolderModel) : Long
    
    fun getAllFolder(): Flow<List<MyFolderModel>>

    suspend fun updateMyFolder(myFolderModel: MyFolderModel)


    suspend fun deleteMyFolder(myFolderModel: MyFolderModel)
    


    suspend fun getMyFolder(id: Int): MyFolderModel


    suspend fun getMyFolderByName(folderName: String): MyFolderModel


    suspend fun addMyPage(myPageModel: MyPageModel) : Long
    suspend fun deleteMyPage(myPageModel: MyPageModel)
    fun  getAllPages(folderId : Long) : Flow<List<MyPageModel>>
    fun getPages() : Flow<List<MyPageModel>>
}