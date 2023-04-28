package com.elkdocs.handwritter.di

import android.app.Application
import androidx.room.Room
import com.elkdocs.handwritter.data.data_source.MyDatabase
import com.elkdocs.handwritter.data.repository.MyFolderRepositoryImp
import com.elkdocs.handwritter.domain.repository.MyFolderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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



//    @Provides
//    @Singleton
//    @Named("myPageRepository")
//    fun provideMyPageRepository(db : MyDatabase) : MyPageRepository {
//        return MyPageRepositoryImp(db.myPageDao())
//    }
}

