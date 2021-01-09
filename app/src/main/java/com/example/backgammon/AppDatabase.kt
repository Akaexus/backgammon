package com.example.backgammon

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(User::class, Score::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun scoreDao(): ScoreDao
}
