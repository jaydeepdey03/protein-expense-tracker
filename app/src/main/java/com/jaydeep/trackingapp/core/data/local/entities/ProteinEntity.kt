package com.jaydeep.trackingapp.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "protein")
data class ProteinEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val remoteId: String? = null,
    val foodName: String,
    val gramsConsumed: Double = 0.0,
    val proteinGrams: Double,
    val calories: Int? = null,
    val note: String? = null,
    val date: String,
    val isSynced: Boolean = false,
)