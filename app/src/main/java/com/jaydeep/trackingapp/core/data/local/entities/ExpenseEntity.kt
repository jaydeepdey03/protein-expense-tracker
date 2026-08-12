package com.jaydeep.trackingapp.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val amount: Double,
    val currency: String,
    val date: String,
    val category: String,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false,      // false = pending sync to server
)