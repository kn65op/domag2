package com.example.domag2.database.database

import android.content.Context
import android.util.Log
import androidx.room.Room

class SqlDatabaseFactory : DatabaseFactory {
    override fun createDatabase(applicationContext: Context): AppDatabase {
        Log.i(DatabaseFactory.LOG_TAG, "SQL")
        return getInstance(applicationContext)
    }

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(applicationContext: Context): AppDatabase = (instance ?: synchronized(this)
        {
            Log.i(DatabaseFactory.LOG_TAG, "get SQL instance")
            instance ?: buildDatabase(applicationContext).also { instance = it }
        })

        private fun buildDatabase(applicationContext: Context) =
            Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).build()
    }
}