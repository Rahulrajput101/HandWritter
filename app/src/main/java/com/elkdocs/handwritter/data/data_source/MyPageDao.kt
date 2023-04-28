//package com.elkdocs.handwritter.data.data_source
//
//import androidx.room.*
//import com.elkdocs.handwritter.domain.model.MyPageModel
//
//@Dao
//interface MyPageDao {
//
//
//
//    @Query("SELECT * FROM my_image WHERE parentId = :parentId")
//    suspend fun getAllMyPageByParentId(parentId: Int): List<MyPageModel>
//}