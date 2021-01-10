package com.example.backgammon

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity

class MatchListAdapter(private val context: Context, private val arrayList: List<Match>, private val userList: List<User>, val loggedUser: User, ctx: Context) : BaseAdapter() {
    private lateinit var player1: TextView
    private lateinit var player2: TextView
    private lateinit var points: TextView
    private lateinit var btn_play: Button
    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getItem(position: Int): Any {
        return arrayList[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getUserById(userID: Int) :User? {
        this.userList.forEach { u ->
            if (u.uid == userID) {
                return u
            }
        }
        return null
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.match_entry, parent, false)
        player1 = convertView.findViewById(R.id.player1)
        player2 = convertView.findViewById(R.id.player2)
        points = convertView.findViewById(R.id.points)
        btn_play = convertView.findViewById(R.id.btn_playbutton)
        val match:Match = this.arrayList[position]

        player1.text = this.getUserById(match.player1_uid)?.login ?: "N/A"
        player2.text = this.getUserById(match.player2_uid)?.login ?: "N/A"
        val point_combinations:HashMap<Int, String> = hashMapOf(
                -1 to "0 - 0",
                0 to "1 - 0",
                1 to "0 - 1"
        )
        points.text = point_combinations[match.winner]
        if (!match.played && (this.loggedUser.uid == match.player1_uid || this.loggedUser.uid == match.player2_uid)) {
            btn_play.setOnClickListener {
                val intent = Intent(context, Backgammon::class.java)
                intent.putExtra("user1", this.getUserById(match.player1_uid))
                intent.putExtra("user2", this.getUserById(match.player2_uid))
                intent.putExtra("mode", "normal")
                intent.putExtra("match", match)
                startActivity(context, intent, null)
                (context as Activity).finish()
            }
        } else {
            (btn_play.parent as LinearLayout).removeViewAt(0)
        }
//        player.text = arrayList[position].username
//        points.text = "${arrayList[position].score} pts"
        return convertView
    }
}