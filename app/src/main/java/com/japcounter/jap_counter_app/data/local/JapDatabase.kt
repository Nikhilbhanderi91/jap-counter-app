package com.japcounter.jap_counter_app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [JapHistoryEntity::class], version = 2, exportSchema = false)
abstract class JapDatabase : RoomDatabase() {
    abstract fun japHistoryDao(): JapHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: JapDatabase? = null

        fun getDatabase(context: Context): JapDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JapDatabase::class.java,
                    "jap_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
