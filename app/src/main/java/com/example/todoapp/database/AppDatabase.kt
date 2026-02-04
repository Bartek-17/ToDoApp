package com.example.todoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todoapp.data.Task

// Main database class for the app
@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Specifies the type converters for Room to use
abstract class AppDatabase : RoomDatabase() {

    // Abstract function that returns the DAO for the task table
    abstract fun taskDao(): TaskDao

    // The companion object allows to have a singleton instance of the database
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
