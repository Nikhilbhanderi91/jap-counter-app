package com.japcounter.jap_counter_app.data.repository

import com.japcounter.jap_counter_app.data.local.JapHistoryDao
import com.japcounter.jap_counter_app.data.local.JapHistoryEntity
import kotlinx.coroutines.flow.Flow

class JapHistoryRepository(private val japHistoryDao: JapHistoryDao) {

    val allHistory: Flow<List<JapHistoryEntity>> = japHistoryDao.getAllHistory()
    val pendingHistory: Flow<List<JapHistoryEntity>> = japHistoryDao.getPendingHistory()
    val completedHistory: Flow<List<JapHistoryEntity>> = japHistoryDao.getCompletedHistory()

    suspend fun insert(record: JapHistoryEntity): Long {
        return japHistoryDao.insert(record)
    }

    suspend fun update(record: JapHistoryEntity) {
        japHistoryDao.update(record)
    }

    suspend fun getById(id: Long): JapHistoryEntity? {
        return japHistoryDao.getById(id)
    }

    suspend fun deleteById(id: Long) {
        japHistoryDao.deleteById(id)
    }

    suspend fun deleteAll() {
        japHistoryDao.deleteAll()
    }
}
