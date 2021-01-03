package com.example.backgammon

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM USER WHERE uid IN (:userIDs)")
    fun loadAllByIds(userIDs: IntArray): List<User>

    @Query("SELECT * FROM user WHERE login=:login")
    fun findByLogin(login: String): User?

    @Insert
    fun insert(user: User) : Long

    @Delete
    fun delete(user: User)
}