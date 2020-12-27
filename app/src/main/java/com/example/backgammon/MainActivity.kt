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
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    lateinit var userDao: UserDao
    suspend fun setupDatabase() {
        this.db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "users"
        ).build()
        this.userDao = db.userDao()
    }
    suspend fun getUser(login: String, password: String): User? {
        var user = this.userDao.findByLogin(login)
        if (user == null) {
            user = registerUser(login, password)
            return user
        } else { // check password
            if (user.passwordHash == this.hashPassword(password)) {
                Log.i("dupa_debug", "Loggedin ${login}:${password}")
                return user
            }
            Log.i("dupa_debug", "Not logged in")
            return null
        }
    }

    suspend fun registerUser(login: String, password: String): User {
        Log.i("dupa_debug", "Registered ${login}:${password}")
        val user: User = User(null, login, this.hashPassword(password))
        this.userDao.insertAll(user)
        return user
    }

    fun hashPassword(pass :String): String {
        val bytes = pass.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", {str, it -> str + "%02x".format(it)})
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        GlobalScope.launch {
            setupDatabase()
        }
        var btn_login = requireViewById<Button>(R.id.btn_login)
        var et_login = requireViewById<TextInputEditText>(R.id.et_login)
        var et_password = requireViewById<TextInputEditText>(R.id.et_password)
        btn_login.setOnClickListener {
            val username = et_login.text.toString()
            val password = et_password.text.toString()
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter your login!", Toast.LENGTH_LONG).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password!", Toast.LENGTH_LONG).show()
            } else {

                val job = GlobalScope.launch {
                    var user = getUser(username, password)
                    if (user == null) {
                        Toast.makeText(this@MainActivity, "Bad login or password!", Toast.LENGTH_LONG).show()
                    } else {
                        val intent = Intent(this@MainActivity, Backgammon::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}