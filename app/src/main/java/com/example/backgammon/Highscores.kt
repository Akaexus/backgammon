package com.example.backgammon

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewStub
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */


class Highscores : AppCompatActivity() {
    lateinit var highscores:List<Score>
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscores)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        GlobalScope.launch {
            this@Highscores.setupDatabase()
            this@Highscores.highscores = this@Highscores.scoreDao.getHighest(50)
            Handler(Looper.getMainLooper()).post {
                this@Highscores.addEntries()
            }
        }

    }

    fun addEntries() {
        var list:ListView = findViewById<ListView>(R.id.lv_highscores)
        list.adapter = ScoreListAdapter(this, this.highscores)
    }

    private lateinit var db: AppDatabase
    lateinit var scoreDao: ScoreDao

    private suspend fun setupDatabase() {
        this.db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "backgammon"
        ).build()
        this.scoreDao = db.scoreDao()
    }
}