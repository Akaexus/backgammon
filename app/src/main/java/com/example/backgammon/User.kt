package com.example.backgammon

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class User (
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "login") val login: String?,
    @ColumnInfo(name = "password_hash") val passwordHash: String?
) : Parcelable
