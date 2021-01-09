package com.example.backgammon

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "score")
class Score (
        @PrimaryKey(autoGenerate = true) var uid: Int,
        @ColumnInfo(name = "userid") val userid: Int,
        @ColumnInfo(name = "username") val username: String,
        @ColumnInfo(name = "score") val score: Int
) : Parcelable {
    constructor(userid: Int, username:String, score: Int) : this(0, userid, username, score)
}
