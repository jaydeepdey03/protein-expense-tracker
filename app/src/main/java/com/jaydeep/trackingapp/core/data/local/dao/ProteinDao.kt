package com.jaydeep.trackingapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jaydeep.trackingapp.core.data.local.entities.ProteinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProteinDao {
    @Query("SELECT * FROM protein ORDER BY date DESC")
    fun getAll(): Flow<List<ProteinEntity>>

    @Query("SELECT * FROM protein WHERE id = :id")
    suspend fun getById(id: Long): ProteinEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(protein: ProteinEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(proteins: List<ProteinEntity>)

    @Update
    suspend fun update(protein: ProteinEntity)

    @Query("DELETE FROM protein WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM protein WHERE isSynced = 0")
    suspend fun getUnsynced(): List<ProteinEntity>

    @Query("UPDATE protein SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: Long)
}
