package com.example.backgammon

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import kotlin.random.Random


data class Game(
        var context: Context,
        var players: Array<Player>,
        var areas: ArrayList<FlexboxLayout>,
        var dices: Array<ImageView>,
        var diceBoxes: Array<FlexboxLayout>,
        var diceBox: LinearLayout,
        var scoreBoxes: Array<LinearLayout>
) {
    // game board
    var board = Array<ArrayList<Pawn>>(24) { i -> ArrayList<Pawn>() }
    var house = Array<ArrayList<Pawn>>(2) { i -> ArrayList<Pawn>() }
    var state:Int = ROLL_DICE_CHOOSE_FIRST_PLAYER
    var currentPlayer:Int = 0 // first player begins

    fun setCurrentState(s: Int) {
        Log.i("bacgammon_debug", "setCurrentState(${s})")
        this.state = s
    }

    fun getCurrentPlayer(): Player {
        return this.players[this.currentPlayer]
    }

    fun nextPlayer() {
        this.currentPlayer = this.currentPlayer.xor(1) // ${} xor 1 = !${}

        // highlight current player in score boxes
        this.players.forEachIndexed { index, _ ->
            val color: Int = Color.parseColor(if (index == this.currentPlayer) "#ffffff" else "#cccccc")
            for(childID in 0 until scoreBoxes[index].childCount) {
                var tv_scoreBoxText: TextView = scoreBoxes[index].getChildAt(childID) as TextView
                tv_scoreBoxText.setTextColor(color)
            }
        }
    }

    fun rollDice(doubleIfSame: Boolean = true): List<Int> {
        var randomValues = List<Int>(2) { Random.nextInt(1, 6)}
        if (doubleIfSame && randomValues[0] == randomValues[1]) { // if two same numbers rolled, double
            randomValues += randomValues
        }
        Log.i("backgammon_debug", randomValues.toString())

        this.dices.forEachIndexed { index, diceImage -> // update roll dice icons
            var id = context.resources.getIdentifier("dice${randomValues[index]}", "drawable", context.packageName)
            var drawable: Drawable? = ResourcesCompat.getDrawable(context.resources, id, null)
            diceImage.setImageDrawable(drawable)
            (this.diceBoxes[this.currentPlayer].getChildAt(index) as ImageView).setImageDrawable(drawable)
        }


        return randomValues
    }


    fun init() {
        val initialPivotPositions: Array<Array<IntArray>> = arrayOf(
            arrayOf( // player 1
                    intArrayOf(6-1, 5), // field number, number of pivots on field
                    intArrayOf(8-1, 3), // arrays starts at 0
                    intArrayOf(13-1, 5),
                    intArrayOf(24-1, 2),
            ),
            arrayOf( // player 2
                    intArrayOf(1-1, 2),
                    intArrayOf(12-1, 5),
                    intArrayOf(17-1, 3),
                    intArrayOf(19-1, 5),
            ),
        )
        initialPivotPositions.forEachIndexed { playerID, pivotPositions ->
            for (pos in pivotPositions) {
                repeat(pos[1]) {
                    var pawn = Pawn(this.context, this.players[playerID])
                    this.board[pos[0]].add(pawn)
                    areas[pos[0]].addView(pawn.element)
                    // https://stackoverflow.com/questions/44874843/remove-imageview-programmatically-from-custom-layout
                    // +
                    // https://stackoverflow.com/questions/15097950/adding-imageview-to-the-layout-programmatically
                }
            }
        }

        // attachListeners
        this.diceBox.setOnClickListener {
            Log.i("backgammon_debug", "dice clicked")
            Log.i("backgammon_debug", "current state: ${this.state}")
            if (this.state == ROLL_DICE_CHOOSE_FIRST_PLAYER) {
                if (this.players[0].dices[0] * this.players[1].dices[0] == 0) { // check if any of players didn't rolled dices
                    this.getCurrentPlayer().dices = this.rollDice(doubleIfSame = false)
                }
                if (this.players[0].dices[0] * this.players[1].dices[0] != 0) { // all players rolled dice, choose who's first
                    if (this.players[0].dices.sum() > this.players[1].dices.sum()) { // if player 1 wins, change to next player
                        this.nextPlayer()
                    }
                    this.setCurrentState(ROLL_DICE)
                    Toast.makeText(context, "${this.getCurrentPlayer().getUsername()} begins", Toast.LENGTH_SHORT).show()
                } else {
                    this.nextPlayer()
                }
            }

            if (this.state == ROLL_DICE) {
                this.getCurrentPlayer().dices = this.rollDice()
//                this.setCurrentState(WAIT_TO_CHOOSE_PAWN)
            }
        }
    }

    companion object {
        const val ROLL_DICE_CHOOSE_FIRST_PLAYER = 0
        const val ROLL_DICE = 1
        const val WAIT_TO_CHOOSE_PAWN = 2
        const val PAWN_CHOSEN_CHOOSE_AREA = 3
    }

}