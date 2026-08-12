package com.jaydeep.trackingapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jaydeep.trackingapp.core.data.local.dao.ExpenseDao
import com.jaydeep.trackingapp.core.data.local.dao.ProteinDao
import com.jaydeep.trackingapp.core.data.local.entities.ExpenseEntity
import com.jaydeep.trackingapp.core.data.local.entities.ProteinEntity

@Database(
    entities = [ExpenseEntity::class, ProteinEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class TrackerDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun proteinDao(): ProteinDao
}