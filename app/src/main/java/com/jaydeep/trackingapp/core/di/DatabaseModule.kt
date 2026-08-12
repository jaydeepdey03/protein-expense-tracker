package com.jaydeep.trackingapp.core.di

import android.content.Context
import androidx.room.Room
import com.jaydeep.trackingapp.core.data.local.TrackerDatabase
import com.jaydeep.trackingapp.core.data.local.dao.ExpenseDao
import com.jaydeep.trackingapp.core.data.local.dao.ProteinDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrackerDatabase =
        Room.databaseBuilder(
            context,
            TrackerDatabase::class.java,
            "tracker.db",
        )
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun provideExpenseDao(db: TrackerDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideProteinDao(db: TrackerDatabase): ProteinDao = db.proteinDao()
}