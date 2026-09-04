package com.japcounter.jap_counter_app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JapHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: JapHistoryEntity): Long

    @Update
    suspend fun update(record: JapHistoryEntity)

    @Query("SELECT * FROM jap_history WHERE id = :id")
    suspend fun getById(id: Long): JapHistoryEntity?

    @Query("SELECT * FROM jap_history ORDER BY completedAt DESC")
    fun getAllHistory(): Flow<List<JapHistoryEntity>>

    @Query("SELECT * FROM jap_history WHERE status = 'PENDING' OR status = 'IN_PROGRESS' ORDER BY completedAt DESC")
    fun getPendingHistory(): Flow<List<JapHistoryEntity>>

    @Query("SELECT * FROM jap_history WHERE status = 'COMPLETED' ORDER BY completedAt DESC")
    fun getCompletedHistory(): Flow<List<JapHistoryEntity>>

    @Query("DELETE FROM jap_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM jap_history")
    suspend fun deleteAll()
}
