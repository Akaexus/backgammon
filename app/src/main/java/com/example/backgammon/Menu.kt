package com.example.backgammon

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi

class Menu : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        var user: User? = intent.getParcelableExtra<User>("user")

        var btn_quit = requireViewById<Button>(R.id.btn_quit)
        btn_quit.setOnClickListener {
            finishAffinity()
        }

    }
}