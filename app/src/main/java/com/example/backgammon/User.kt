package com.example.backgammon

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user")
class User (
        @PrimaryKey(autoGenerate = true) var uid: Int,
        @ColumnInfo(name = "login") val login: String,
        @ColumnInfo(name = "password_hash") val passwordHash: String
) : Parcelable {
    constructor(login: String, passwordHash: String) : this(0, login, passwordHash)
}
