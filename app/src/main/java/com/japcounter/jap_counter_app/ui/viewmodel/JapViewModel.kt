package com.japcounter.jap_counter_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.japcounter.jap_counter_app.data.JapPreferences
import com.japcounter.jap_counter_app.data.JapSession
import com.japcounter.jap_counter_app.data.local.JapDatabase
import com.japcounter.jap_counter_app.data.local.JapHistoryEntity
import com.japcounter.jap_counter_app.data.repository.JapHistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JapViewModel(application: Application) : AndroidViewModel(application) {

    private val japPreferences = JapPreferences(application)
    private val database = JapDatabase.getDatabase(application)
    private val repository = JapHistoryRepository(database.japHistoryDao())

    val sessionState: StateFlow<JapSession> = japPreferences.sessionFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = JapSession(
            userName = null,
            mantraName = "",
            targetCount = 108,
            currentCount = 0,
            isSessionActive = false,
            activeTaskId = 0L
        )
    )

    val historyList = repository.allHistory
    val pendingHistory = repository.pendingHistory
    val completedHistory = repository.completedHistory

    fun saveUserName(name: String) {
        viewModelScope.launch {
            japPreferences.saveUserName(name)
        }
    }

    fun startNewSession(mantraName: String, targetCount: Int) {
        viewModelScope.launch {
            val record = JapHistoryEntity(
                userName = sessionState.value.userName ?: "",
                japName = mantraName,
                targetCount = targetCount,
                completedCount = 0,
                completedAt = System.currentTimeMillis(),
                status = "PENDING"
            )
            val newTaskId = repository.insert(record)
            japPreferences.startNewSession(mantraName, targetCount, newTaskId)
        }
    }

    fun resumeTask(task: JapHistoryEntity) {
        viewModelScope.launch {
            japPreferences.loadSession(
                mantraName = task.japName,
                targetCount = task.targetCount,
                currentCount = task.completedCount,
                taskId = task.id
            )
        }
    }

    fun incrementCounter() {
        val currentSession = sessionState.value
        if (currentSession.currentCount < currentSession.targetCount) {
            val newCount = currentSession.currentCount + 1
            viewModelScope.launch {
                japPreferences.updateCurrentCount(newCount)
                
                val isCompleted = newCount >= currentSession.targetCount
                val newStatus = if (isCompleted) "COMPLETED" else "IN_PROGRESS"

                if (currentSession.activeTaskId > 0) {
                    val existing = repository.getById(currentSession.activeTaskId)
                    if (existing != null) {
                        repository.update(
                            existing.copy(
                                completedCount = newCount,
                                completedAt = System.currentTimeMillis(),
                                status = newStatus
                            )
                        )
                    } else {
                        val record = JapHistoryEntity(
                            id = currentSession.activeTaskId,
                            userName = currentSession.userName ?: "",
                            japName = currentSession.mantraName,
                            targetCount = currentSession.targetCount,
                            completedCount = newCount,
                            completedAt = System.currentTimeMillis(),
                            status = newStatus
                        )
                        repository.insert(record)
                    }
                } else {
                    val record = JapHistoryEntity(
                        userName = currentSession.userName ?: "",
                        japName = currentSession.mantraName,
                        targetCount = currentSession.targetCount,
                        completedCount = newCount,
                        completedAt = System.currentTimeMillis(),
                        status = newStatus
                    )
                    val generatedId = repository.insert(record)
                    japPreferences.setActiveTaskId(generatedId)
                }
            }
        }
    }

    fun resetSession() {
        viewModelScope.launch {
            val currentSession = sessionState.value
            if (currentSession.activeTaskId > 0) {
                val existing = repository.getById(currentSession.activeTaskId)
                if (existing != null) {
                    repository.update(
                        existing.copy(
                            completedCount = 0,
                            status = "PENDING"
                        )
                    )
                }
            }
            japPreferences.resetSession()
        }
    }

    fun updateMantraAndTarget(mantraName: String, targetCount: Int) {
        viewModelScope.launch {
            japPreferences.updateMantraAndTarget(mantraName, targetCount)
            val currentSession = sessionState.value
            if (currentSession.activeTaskId > 0) {
                val existing = repository.getById(currentSession.activeTaskId)
                if (existing != null) {
                    repository.update(
                        existing.copy(
                            japName = mantraName,
                            targetCount = targetCount
                        )
                    )
                }
            }
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            japPreferences.clearSession()
        }
    }

    fun deleteHistoryRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
