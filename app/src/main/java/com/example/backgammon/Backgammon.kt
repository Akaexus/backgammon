package com.example.backgammon

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Backgammon : AppCompatActivity() {
    var match:Match? = null
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backgammon)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        var btn_menu:Button = requireViewById<Button>(R.id.btn_menu)
        btn_menu.setOnClickListener {
            finish()
        }


        var user1: User? = intent.getParcelableExtra<User>("user1")
        var user2: User? = intent.getParcelableExtra<User>("user2")
        this.match = intent.getParcelableExtra<Match>("match")
        Log.i("match_id", match?.uid.toString())

        var mode: String? = intent.getStringExtra("mode")
        var player1 = Player(user1, "blue")
//        var player2 = Player(user2, "red", if (mode != null && mode == "ai") Player.MODE_AI else Player.MODE_PLAYER)
        var player2 = Player(user2, "red", Player.MODE_AI)

        // get areas
        var areas :HashMap<Int, Area> = hashMapOf()
        areas[-1] = Area(requireViewById<FlexboxLayout>(R.id.dock_minus_1), this.applicationContext, Area.TYPE_DOCK)
        areas[24] = Area(requireViewById<FlexboxLayout>(R.id.dock_24), this.applicationContext, Area.TYPE_DOCK)
        for(i in 0..23) {
            var id: Int = resources.getIdentifier("ll_area_$i", "id", this.packageName)
            areas[i] = (Area(requireViewById<FlexboxLayout>(id), this.applicationContext))
        }


        // get dices
        var dices :Array<ImageView> = arrayOf(
                requireViewById<ImageView>(R.id.dice1),
                requireViewById<ImageView>(R.id.dice2)
        )

        var diceBoxes :Array<FlexboxLayout> = arrayOf(
                requireViewById<FlexboxLayout>(R.id.player1_dices),
                requireViewById<FlexboxLayout>(R.id.player2_dices)
        )

        // scoreboxes
        var scoreBoxes :Array<LinearLayout> = arrayOf(
                requireViewById<LinearLayout>(R.id.scorebox1),
                requireViewById<LinearLayout>(R.id.scorebox2)
        )
        player1.addScorebox(requireViewById<LinearLayout>(R.id.scorebox1))
        player2.addScorebox(requireViewById<LinearLayout>(R.id.scorebox2))


        var game :Game = Game(
                baseContext,
                arrayOf(
                    player1,
                    player2
                ),
                areas,
                dices,
                diceBoxes,
                requireViewById<LinearLayout>(R.id.dicebox),
                scoreBoxes,
                requireViewById<FlexboxLayout>(R.id.band),
                ::onFinish,
                requireViewById<TextView>(R.id.timer),
                requireViewById<ImageButton>(R.id.btn_pawnSkin)
        )
        game.init()
    }

    private lateinit var db: AppDatabase
    lateinit var scoreDao: ScoreDao

    suspend fun setupDatabase() {
        this.db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "backgammon"
        ).build()
        this.scoreDao = db.scoreDao()
    }

    fun onFinish(players:Array<Player>, winner:Int) {
        Toast.makeText(baseContext, "Player ${players[winner].getUsername()} wins with ${players[winner].score} points!", Toast.LENGTH_LONG).show()
        GlobalScope.launch {
            this@Backgammon.setupDatabase()
            if (players[winner].user != null) {
                this@Backgammon.scoreDao.insert(Score(players[winner].user!!.uid, players[winner].user!!.login, players[winner].score))
            }
            Log.i("match_heh", "match_heh")
            if (this@Backgammon.match != null) {
                Log.i("match_heh", "match_heh")
                this@Backgammon.match!!.winner = winner
                this@Backgammon.match!!.played = true
                Log.i("match_updated", "match_updated")
                var before:Match = this@Backgammon.db.matchDao().findByID(this@Backgammon.match!!.uid)!!
                Log.i("match_updated", "uid: ${before.uid}, winner: ${before.winner}, played: ${before.played}")
                this@Backgammon.db.matchDao().updateMatch(this@Backgammon.match!!)
                var after:Match = this@Backgammon.db.matchDao().findByID(this@Backgammon.match!!.uid)!!
                Log.i("match_updated", "uid: ${after.uid}, winner: ${after.winner}, played: ${after.played}")
            }
        }
        finish()
    }
}