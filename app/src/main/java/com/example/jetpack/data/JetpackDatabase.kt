package com.example.jetpack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [JetpackModel::class], version = 1)
abstract class JetpackDatabase : RoomDatabase() {
    abstract fun jetpackDao(): JetpackDao

    companion object {
        @Volatile private var instance: JetpackDatabase? = null
        private val LOCK = Any()

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            JetpackDatabase::class.java,
            "jetpackdatabase"
        ).build()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }
    }
}