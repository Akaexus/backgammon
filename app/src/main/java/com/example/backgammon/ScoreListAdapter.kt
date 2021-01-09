package com.example.backgammon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView

class ScoreListAdapter(private val context: Context, private val arrayList: List<Score>) : BaseAdapter() {
    private lateinit var player: TextView
    private lateinit var points: TextView
    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.highscores_entry, parent, false)
        player = convertView.findViewById(R.id.nick)
        points = convertView.findViewById(R.id.points)
        player.text = arrayList[position].username
        points.text = "${arrayList[position].score} pts"
        return convertView
    }
}