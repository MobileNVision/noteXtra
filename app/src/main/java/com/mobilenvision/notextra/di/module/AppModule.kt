package com.mobilenvision.notextra.di.module


import android.app.Application
import android.content.Context
import androidx.room.Room.databaseBuilder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mobilenvision.notextra.data.AppDataManager
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.data.local.db.AppDatabase
import com.mobilenvision.notextra.data.local.db.AppDbHelper
import com.mobilenvision.notextra.data.local.db.DbHelper
import com.mobilenvision.notextra.data.local.prefs.AppPreferencesHelper
import com.mobilenvision.notextra.data.local.prefs.PreferencesHelper
import com.mobilenvision.notextra.di.DatabaseInfo
import com.mobilenvision.notextra.di.PreferenceInfo
import com.mobilenvision.notextra.utils.AppConstants
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @DatabaseInfo
    fun provideDatabaseName(): String {
        return AppConstants.DB_NAME
    }
    @Provides
    @Singleton
    fun provideAppDatabase(@DatabaseInfo dbName: String, context: Context): AppDatabase {
        return databaseBuilder(
            context,
            AppDatabase::class.java,
            dbName
        ).fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideDataManager(appDataManager: AppDataManager): DataManager {
        return appDataManager
    }

    @Provides
    @Singleton
    fun providePreferencesHelper(appPreferencesHelper: AppPreferencesHelper): PreferencesHelper {
        return appPreferencesHelper
    }

    @Provides
    @Singleton
    fun provideDbHelper(appDbHelper: AppDbHelper): DbHelper {
        return appDbHelper
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

    @Provides
    @PreferenceInfo
    fun providePreferenceName(): String? {
        return AppConstants.PREF_NAME
    }

}
