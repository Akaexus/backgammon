package com.example.backgammon

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Query("SELECT * FROM score")
    fun getAll(): List<Score>

    @Query("SELECT * FROM score WHERE uid IN (:uid)")
    fun loadAllByIds(uid: IntArray): List<Score>

    @Query("SELECT * FROM score WHERE userid=:userid")
    fun findByUID(userid: Int): List<Score>?

    @Query("SELECT * FROM score ORDER BY score DESC limit :limit")
    fun getHighest(limit: Int = 50): List<Score>

    @Insert
    fun insert(score: Score) : Long

    @Delete
    fun delete(score: Score)
}