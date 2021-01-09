package com.example.backgammon

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi

class Menu : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        var user: User? = intent.getParcelableExtra<User>("user")

        var btn_quit = requireViewById<Button>(R.id.btn_quit)
        btn_quit.setOnClickListener {
            finishAffinity()
        }

        var btn_play1vsai = requireViewById<Button>(R.id.btn_play1vsai)
        btn_play1vsai.setOnClickListener {
            val intent = Intent(this, Backgammon::class.java)
            intent.putExtra("user1", user)
            intent.putExtra("mode", "ai")
            startActivity(intent)
            finish()
        }
        var btn_play1vs1 = requireViewById<Button>(R.id.btn_play1vs1)
        btn_play1vs1.setOnClickListener {
            val intent = Intent(this, Backgammon::class.java)
            intent.putExtra("user1", user)
            intent.putExtra("mode", "normal")
            startActivity(intent)
            finish()
        }

    }
}