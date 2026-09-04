package com.japcounter.jap_counter_app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jap_history")
data class JapHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userName: String,
    val japName: String,
    val targetCount: Int,
    val completedCount: Int,
    val completedAt: Long,
    val status: String = "COMPLETED" // "PENDING", "IN_PROGRESS", "COMPLETED"
)
