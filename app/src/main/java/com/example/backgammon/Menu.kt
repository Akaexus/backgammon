package com.example.backgammon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        var user: User? = intent.getParcelableExtra<User>("user")

        if (user != null) {
            Log.i("backgammon_debug", user.uid.toString())
            Toast.makeText(this, user.login, Toast.LENGTH_LONG).show()
        }
    }
}