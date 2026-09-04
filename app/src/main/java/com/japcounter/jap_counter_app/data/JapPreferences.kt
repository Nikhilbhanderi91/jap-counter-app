package com.japcounter.jap_counter_app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jap_settings")

data class JapSession(
    val userName: String?,
    val mantraName: String,
    val targetCount: Int,
    val currentCount: Int,
    val isSessionActive: Boolean,
    val activeTaskId: Long = 0L
)

class JapPreferences(private val context: Context) {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val MANTRA_NAME = stringPreferencesKey("mantra_name")
        val TARGET_COUNT = intPreferencesKey("target_count")
        val CURRENT_COUNT = intPreferencesKey("current_count")
        val IS_SESSION_ACTIVE = booleanPreferencesKey("is_session_active")
        val ACTIVE_TASK_ID = longPreferencesKey("active_task_id")
    }

    val sessionFlow: Flow<JapSession> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val userName = preferences[PreferencesKeys.USER_NAME]
            val mantraName = preferences[PreferencesKeys.MANTRA_NAME] ?: ""
            val targetCount = preferences[PreferencesKeys.TARGET_COUNT] ?: 108
            val currentCount = preferences[PreferencesKeys.CURRENT_COUNT] ?: 0
            val isSessionActive = preferences[PreferencesKeys.IS_SESSION_ACTIVE] ?: false
            val activeTaskId = preferences[PreferencesKeys.ACTIVE_TASK_ID] ?: 0L
            
            JapSession(userName, mantraName, targetCount, currentCount, isSessionActive, activeTaskId)
        }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun startNewSession(mantraName: String, targetCount: Int, taskId: Long = 0L) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MANTRA_NAME] = mantraName
            preferences[PreferencesKeys.TARGET_COUNT] = targetCount
            preferences[PreferencesKeys.CURRENT_COUNT] = 0
            preferences[PreferencesKeys.IS_SESSION_ACTIVE] = true
            preferences[PreferencesKeys.ACTIVE_TASK_ID] = taskId
        }
    }

    suspend fun loadSession(mantraName: String, targetCount: Int, currentCount: Int, taskId: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MANTRA_NAME] = mantraName
            preferences[PreferencesKeys.TARGET_COUNT] = targetCount
            preferences[PreferencesKeys.CURRENT_COUNT] = currentCount
            preferences[PreferencesKeys.IS_SESSION_ACTIVE] = true
            preferences[PreferencesKeys.ACTIVE_TASK_ID] = taskId
        }
    }

    suspend fun updateMantraAndTarget(mantraName: String, targetCount: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MANTRA_NAME] = mantraName
            preferences[PreferencesKeys.TARGET_COUNT] = targetCount
            preferences[PreferencesKeys.IS_SESSION_ACTIVE] = true
        }
    }

    suspend fun updateCurrentCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_COUNT] = count
        }
    }

    suspend fun setActiveTaskId(taskId: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACTIVE_TASK_ID] = taskId
        }
    }

    suspend fun resetSession() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_COUNT] = 0
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MANTRA_NAME] = ""
            preferences[PreferencesKeys.TARGET_COUNT] = 108
            preferences[PreferencesKeys.CURRENT_COUNT] = 0
            preferences[PreferencesKeys.IS_SESSION_ACTIVE] = false
            preferences[PreferencesKeys.ACTIVE_TASK_ID] = 0L
        }
    }
}
