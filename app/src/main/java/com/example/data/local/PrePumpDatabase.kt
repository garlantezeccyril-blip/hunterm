package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.SignalMemoryEntity

@Database(
    entities = [SignalMemoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PrePumpDatabase : RoomDatabase() {
    abstract fun signalDao(): SignalDao

    companion object {
        @Volatile
        private var INSTANCE: PrePumpDatabase? = null

        fun getDatabase(context: Context): PrePumpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PrePumpDatabase::class.java,
                    "pre_pump_engine.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
