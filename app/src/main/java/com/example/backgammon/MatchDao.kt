package com.example.backgammon

import androidx.room.*

@Dao
interface MatchDao {
    @Query("SELECT * FROM 'match'")
    fun getAll(): List<Match>

    @Update
    fun updateMatch(match: Match)

    @Query("SELECT * FROM 'match' WHERE uid=:uid")
    fun findByID(uid: Int): Match?

    @Query("DELETE FROM 'match'")
    fun deleteAll()

    @Insert
    fun insert(match: Match) : Long

    @Delete
    fun delete(match: Match)
}