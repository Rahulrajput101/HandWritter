package com.elkdocs.handwritter.data.data_source

import androidx.room.*
import com.elkdocs.handwritter.domain.model.MyFolderModel
import com.elkdocs.handwritter.domain.model.MyPageModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDao {

    @Upsert
    suspend fun addMyFolder(myFolderModel: MyFolderModel): Long

    @Query("DELETE FROM my_folders WHERE folderId = :folderId")
    suspend fun deleteMyFolder(folderId: Long)


    @Query("Select * from my_folders")
    fun getAllFolder(): Flow<List<MyFolderModel>>

    @Query("SELECT * FROM my_folders WHERE folderId = :id")
    suspend fun getMyFolder(id: Long): MyFolderModel

    @Query("SELECT * FROM my_folders WHERE folderName = :folderName LIMIT 1")
    suspend fun getMyFolderByName(folderName: String): MyFolderModel?

    @Upsert
    suspend fun addMyPage(myPageModel: MyPageModel): Long

    @Delete
    suspend fun deleteMyPage(myPageModel: MyPageModel)

    @Query("DELETE FROM my_pages WHERE folderId = :folderId")
    suspend fun deletePagesByFolderId(folderId: Long)

    @Query("SELECT * FROM my_pages WHERE folderId = :folderId")
    fun getAllPages(folderId: Long): Flow<List<MyPageModel>>

    @Query("SELECT * FROM  my_pages WHERE pageId = :pageId")
    suspend fun getPageById(pageId: Long): MyPageModel

    @Query("SELECT * FROM my_pages")
    fun getPages(): Flow<List<MyPageModel>>

    @Transaction
    suspend fun deleteMyFolderWithPages(folderId: Long) {
        // Delete the folder itself
//        val folder = getMyFolder(folderId)
        deleteMyFolder(folderId)

        // Delete all pages associated with the folder
        deletePagesByFolderId(folderId)
    }

    @Query("UPDATE my_folders SET pageCount = :pageCount WHERE folderId = :folderId")
    suspend fun updateFolderPageCount(folderId: Long, pageCount: Int)

    @Query("UPDATE my_folders SET folderName = :folderName WHERE folderId = :folderId")
    suspend fun updateFolderName(folderName: String, folderId: Long)

    @Query("SELECT * FROM my_folders WHERE folderName LIKE '%' || :folderName || '%' ")
    fun searchFolderByQuery(folderName: String): Flow<List<MyFolderModel>>


}