package com.example.joinme.data.entities

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room

@Database(entities = [User::class, Activity::class, UserActivityJoin::class], version = 2)

abstract class AppDatabase : RoomDatabase() {
    abstract fun activitiesDao(): ActivitiesDao
    abstract fun userDao(): UserDao
    abstract fun userActivityJoinDao(): UserActivityJoinDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "joinme_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}