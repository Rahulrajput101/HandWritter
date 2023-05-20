package com.elkdocs.handwritter.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.elkdocs.handwritter.data.data_source.MyDatabase
import com.elkdocs.handwritter.data.repository.MyFolderRepositoryImp
import com.elkdocs.handwritter.domain.repository.MyFolderRepository
import com.elkdocs.handwritter.domain.use_cases.DrawLine
import com.elkdocs.handwritter.util.Constant.IS_LINEAR
import com.elkdocs.handwritter.util.Constant.SHARED_PREFERENCE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideMyDatabase(app: Application): MyDatabase {
        return Room.databaseBuilder(
            app,
            MyDatabase::class.java,
            MyDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideMyFolderRepository(db : MyDatabase) : MyFolderRepository {
        return MyFolderRepositoryImp(db.myFolderDao())
    }

    @Provides
    @Singleton
    fun provideDrawLine(): DrawLine {
        return DrawLine()
    }

    @Provides
    @Singleton
    fun provideSharedPreferneces(
        @ApplicationContext app : Context
    ) = app.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideTypeofRecyclerView(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(IS_LINEAR,false)

//    @Provides
//    @Singleton
//    @Named("myPageRepository")
//    fun provideMyPageRepository(db : MyDatabase) : MyPageRepository {
//        return MyPageRepositoryImp(db.myPageDao())
//    }
}

