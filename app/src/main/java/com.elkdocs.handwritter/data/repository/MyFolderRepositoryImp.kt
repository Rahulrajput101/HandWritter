package com.elkdocs.handwritter.data.repository

import com.elkdocs.handwritter.data.data_source.MyFolderDao
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel
import com.elkdocs.handwritter.domain.repository.MyFolderRepository
import kotlinx.coroutines.flow.Flow

class MyFolderRepositoryImp(
    private val myFolderDao: MyFolderDao,
) : MyFolderRepository {
    override suspend fun addMyFolder(myFolderModel: MyFolderModel) : Long{
        return myFolderDao.addMyFolder(myFolderModel)
    }
    
    override fun getAllFolder(): Flow<List<MyFolderModel>> {
        return myFolderDao.getAllFolder()
    }
    
    override suspend fun updateMyFolder(myFolderModel: MyFolderModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMyFolder(myFolderModel: MyFolderModel) {
    }
    

    override suspend fun getMyFolder(id: Long): MyFolderModel {
       return myFolderDao.getMyFolder(id)
    }

    override suspend fun getMyFolderByName(folderName: String): MyFolderModel {
        TODO("Not yet implemented")
    }

    override suspend fun addMyPage(myPageModel: MyPageModel): Long {
        return myFolderDao.addMyPage(myPageModel)
    }

    override suspend fun deleteMyPage(myPageModel: MyPageModel) {
        myFolderDao.deleteMyPage(myPageModel)
    }



    override  fun getAllPages(folderId: Long): Flow<List<MyPageModel>> {
      return myFolderDao.getAllPages(folderId)
    }

    override fun getPages(): Flow<List<MyPageModel>> {
        return myFolderDao.getPages()
    }

    override suspend fun deleteMyFolderWithPages(myFolderModel: MyFolderModel) {
        myFolderDao.deleteMyFolderWithPages(myFolderModel)
    }

    override suspend fun updateFolderPageCount(folderId: Long, pageCount: Int) {
        myFolderDao.updateFolderPageCount(folderId,pageCount)
    }


}