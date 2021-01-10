package com.example.backgammon

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(User::class, Score::class, Match::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun scoreDao(): ScoreDao
    abstract fun matchDao(): MatchDao
}
