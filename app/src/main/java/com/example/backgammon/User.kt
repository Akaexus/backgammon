package com.example.backgammon

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "login") val login: String?,
    @ColumnInfo(name = "password_hash") val passwordHash: String?
)
