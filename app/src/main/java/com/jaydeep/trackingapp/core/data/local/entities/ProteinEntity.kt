package com.jaydeep.trackingapp.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "protein")
data class ProteinEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val remoteId: String? = null,
    val foodName: String,
    val proteinGrams: Double,
    val calories: Int?,
    val note: String?,
    val date: String,           // stored as ISO-8601 string e.g. "2025-08-12"
    val isSynced: Boolean = false,
)
