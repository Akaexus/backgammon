package com.example.backgammon

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Tournament : AppCompatActivity() {
    lateinit var lv_matches:ListView
    lateinit var lv_score:ListView
    lateinit var loggedUser:User
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournament)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        this.loggedUser = intent.getParcelableExtra<User>("loggedUser")!!

        GlobalScope.launch {
            this@Tournament.setupDatabase()
            this@Tournament.renderMatches(this@Tournament.matchDao.getAll(), this@Tournament.users)
        }

        val btn_set_players: Button = requireViewById<Button>(R.id.btn_set_players)
        btn_set_players.setOnClickListener {

            val dialog = UserDialog()
            dialog.users = this.users
            dialog.show(supportFragmentManager,"tag")
            dialog.callback = ::parseUsersFromDialogs
        }

        this.lv_matches = requireViewById<ListView>(R.id.lv_matches)
        this.lv_score = requireViewById<ListView>(R.id.lv_score)
    }

    fun getUserById(uid:Int) :User? {
        for (user in users) {
            if (user.uid == uid) {
                return user
            }
        }
        return null
    }

    fun renderMatches(matches:List<Match>, users:List<User>) {
        lv_matches.adapter = MatchListAdapter(this, matches, users, this.loggedUser, baseContext)
        var userScores:HashMap<Int, Int> = hashMapOf()
        matches.forEach { match ->
            if (match.played) {
                if (match.player1_uid !in userScores) {
                    userScores[match.player1_uid] = 0
                }
                if (match.player2_uid !in userScores) {
                    userScores[match.player2_uid] = 0
                }
                if (match.winner == 1) {
                    userScores[match.player2_uid] = userScores[match.player2_uid]!!.plus(1)
                } else {
                    userScores[match.player1_uid] = userScores[match.player1_uid]!!.plus(1)
                }
            }
        }
        var finalScores:MutableList<List<*>> = mutableListOf()
        for (key in userScores.keys) {
            var userLogin:String = this.getUserById(key)!!.login
            finalScores.add(listOf(userLogin, userScores[key]) as List<*>)
        }
        finalScores.sortedByDescending { it -> it[1] as Comparable<Any> }
        this.lv_score.adapter = TournamentScoreListAdapter(this, finalScores)

    }

    private lateinit var userDB: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var matchDao: MatchDao
    private lateinit var users: List<User>
    private suspend fun setupDatabase() {
        this.userDB = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "backgammon"
        ).build()
        this.userDao = userDB.userDao()
        this.users = this.userDao.getAll()
        this.matchDao = this.userDB.matchDao()
    }

    fun parseUsersFromDialogs(users:List<User>) {
        GlobalScope.launch {
            var matches:MutableList<Match> = mutableListOf()
            this@Tournament.matchDao.deleteAll()
            users.forEachIndexed { i, user1 ->
                users.forEachIndexed { j, user2 ->
                    if (j < i) {
                        val match:Match = Match(user1.uid, user2.uid, -1, false)
                        this@Tournament.matchDao.insert(match)
                        matches.add(match)
                    }
                }
            }


            Handler(Looper.getMainLooper()).post {
                this@Tournament.renderMatches(matches, this@Tournament.users)
            }
        }
    }
}