package com.example.backgammon

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "match")
class Match (
        @PrimaryKey(autoGenerate = true) var uid: Int,
        @ColumnInfo(name = "player1_uid") val player1_uid: Int,
        @ColumnInfo(name = "player2_uid") val player2_uid: Int,
        @ColumnInfo(name = "winner") var winner: Int,
        @ColumnInfo(name = "played") var played: Boolean,
) : Parcelable {
    constructor(player1_uid: Int, player2_uid: Int, winner: Int, played: Boolean) : this(0, player1_uid, player2_uid, winner, played)
}
