package com.example.backgammon

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.google.android.flexbox.FlexboxLayout

class Backgammon : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backgammon)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        var user: User? = intent.getParcelableExtra<User>("user")
        var player1 = Player(user, "blue")
        var player2 = Player(null, "red")

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
                requireViewById<FlexboxLayout>(R.id.band)
        )
        game.init()
    }
}