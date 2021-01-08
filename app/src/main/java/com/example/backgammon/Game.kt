package com.example.backgammon

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.android.flexbox.FlexboxLayout
import kotlin.collections.ArrayList
import kotlin.random.Random


data class Game(
        var context: Context,
        var players: Array<Player>,
        var areas: ArrayList<Area>,
        var dices: Array<ImageView>,
        var diceBoxes: Array<FlexboxLayout>,
        var diceBox: LinearLayout,
        var scoreBoxes: Array<LinearLayout>,
        var bandElement: FlexboxLayout
) {
    // game board
    var house = Array<ArrayList<Pawn>>(2) { _ -> ArrayList<Pawn>() }
    var state:Int = ROLL_DICE_CHOOSE_FIRST_PLAYER
    var currentPlayer:Int = 0 // first player begins
    var possibleMoves:HashMap<Int, ArrayList<Int>> = hashMapOf()
    var sourceAreaID:Int = 0
    var band :ArrayList<Pawn> = arrayListOf()

    fun setCurrentState(s: Int) {
        Log.i("bacgammon_debug", "setCurrentState(${s})")
        if (s == ROLL_DICE || s == ROLL_DICE_CHOOSE_FIRST_PLAYER) {
            this.unhideDices()
        }
        if (this.state == ROLL_DICE or ROLL_DICE_CHOOSE_FIRST_PLAYER) {
            this.hideDices()
        }
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

    fun rollDice(doubleIfSame: Boolean = true): ArrayList<Int> {
        val randomValues = List<Int>(2) { Random.nextInt(1, 6)} as ArrayList<Int>
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

    fun addToBand(pawn: Pawn) {
        this.band.add(pawn)
        this.bandElement.addView(pawn.element)
    }

    fun playerPawnsInBand(): Boolean {
        val currentPlayer:Player = this.getCurrentPlayer()
        for (pawn in this.band) {
            if (pawn.player == currentPlayer) {
                return true
            }
        }
        return false
    }

    fun getLastPawnInBand(): Pawn? {
        var currentPlayer:Player = this.getCurrentPlayer()
        for (i in this.band.size-1 downTo 0) {
            if (this.band[i].player == currentPlayer) {
                return this.band[i]
            }
        }
        return null
    }

    fun popFromBand() : Pawn? {
        var currentPlayer:Player = this.getCurrentPlayer()
        for (i in this.band.size-1 downTo 0) {
            if (this.band[i].player == currentPlayer) {
                val pawn:Pawn = this.band[i]
                this.bandElement.removeViewAt(i)
                this.band.removeAt(i)
                return pawn
            }
        }
        return null
    }


    fun generatePossibleMoves(ds:MutableSet<ArrayList<Int>>, areaID:Int, pawn:Pawn) :HashMap<Int, ArrayList<Int>> {
        var pm:HashMap<Int, ArrayList<Int>> = hashMapOf()
        ds.forEach { diceset ->
            var candidateAreaID = diceset.sum() * this.getCurrentPlayer().direction + areaID
            if (candidateAreaID in 0 until this.areas.size) {
                if (this.areas[candidateAreaID].canLetPawn(pawn)) {
                    pm[candidateAreaID] = diceset
                }
            }
        }
        return pm
    }

    fun hideDices() {
        Log.i("HIDE", "HIDE")
        this.diceBox.visibility = View.INVISIBLE
    }

    fun unhideDices() {
        Log.i("UNHIDE", "UNHIDE")
        this.diceBox.visibility = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun init() {
        this.players[1].direction = -1
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
                    this.areas[pos[0]].addPawn(pawn)
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
                if (this.playerPawnsInBand()) {
                    this.setCurrentState(PAWN_IN_BAND_CLICK_ON_PAWN)
                } else {
                    this.setCurrentState(WAIT_TO_CHOOSE_PAWN)
                }
            }
        }



        this.areas.forEach { area ->
            area.element.setOnClickListener { elem ->
//                if (this.playerPawnsInBand()) {
//                    return@setOnClickListener
//                }
                val clickedAreaID:Int = this.context.resources.getResourceEntryName(elem.id).filter { it.isDigit() }.toInt()
                val clickedArea = this.areas[clickedAreaID]

                // CHOOSE PAWN TO MOVE
                if (this.state == WAIT_TO_CHOOSE_PAWN) {
                    if (clickedArea.getOwner() == this.getCurrentPlayer()) { // allow to select only own pawns
                        this.possibleMoves = this.generatePossibleMoves(this.getCurrentPlayer().diceset, clickedAreaID, clickedArea.lastPawn()!!)
                        this.possibleMoves.forEach { areaID, diceset ->
                            this.areas[areaID].highlight()
                        }
                        if (this.possibleMoves.size > 0) {
                            this.sourceAreaID = clickedAreaID
                            clickedArea.lastPawn()!!.highlight()
                            this.setCurrentState(PAWN_CHOSEN_CHOOSE_AREA)
                        }
                    }
                }

                if (this.state == PAWN_CHOSEN_CHOOSE_AREA) {
                    if (clickedAreaID in this.possibleMoves) {
                        val pawn:Pawn = this.areas[this.sourceAreaID].pop()!!
                        pawn.unHighlight()
                        val leftOverPawn:Pawn? = clickedArea.addPawn(pawn)
                        // add leftover pawn to the band
                        if (leftOverPawn != null) {
                            this.addToBand(leftOverPawn)
                        }
                        for (key in this.possibleMoves.keys) {
                            this.areas[key].unHighlight()
                        }
                        this.possibleMoves[clickedAreaID]?.forEach { diceNumber ->
                            this.getCurrentPlayer().dices.remove(diceNumber)
                        }
                        if (this.getCurrentPlayer().dices.size > 0) {
                            this.setCurrentState(WAIT_TO_CHOOSE_PAWN)
                        } else {
                            this.setCurrentState(ROLL_DICE)
                            this.nextPlayer()
                        }
                    }
                }

                if (this.state == PAWN_IN_BAND_CHOOSE_AREA) {
                    if (clickedAreaID in this.possibleMoves) {
                        var pawn:Pawn = this.popFromBand()!!
                        pawn.unHighlight()
                        var leftOverPawn:Pawn? = clickedArea.addPawn(pawn)
                        if (leftOverPawn != null) {
                            this.addToBand(leftOverPawn)
                        }
                        for (key in this.possibleMoves.keys) {
                            this.areas[key].unHighlight()
                        }
                        this.possibleMoves[clickedAreaID]?.forEach { diceNumber ->
                            this.getCurrentPlayer().dices.remove(diceNumber)
                        }
                        if (this.getCurrentPlayer().dices.size > 0) {
                            this.setCurrentState(WAIT_TO_CHOOSE_PAWN)
                        } else {
                            this.setCurrentState(ROLL_DICE)
                            this.nextPlayer()
                        }
                    }
                }
            }
        }

        this.bandElement.setOnClickListener {
            if (this.state == PAWN_IN_BAND_CLICK_ON_PAWN) {
                val pawn:Pawn = this.getLastPawnInBand()!!
                pawn.highlight()
                val player:Player = this.getCurrentPlayer()
                val startArea = if (player.direction == 1) -1 else this.areas.size
                this.possibleMoves = this.generatePossibleMoves(this.getCurrentPlayer().diceset, startArea, pawn)
                this.possibleMoves.forEach { areaID, _ ->
                    this.areas[areaID].highlight()
                }
                this.setCurrentState(PAWN_IN_BAND_CHOOSE_AREA)
            }
        }
    }

    companion object {
        const val ROLL_DICE_CHOOSE_FIRST_PLAYER = 0
        const val ROLL_DICE = 1
        const val WAIT_TO_CHOOSE_PAWN = 2
        const val PAWN_CHOSEN_CHOOSE_AREA = 3
        const val PAWN_IN_BAND_CLICK_ON_PAWN = 4
        const val PAWN_IN_BAND_CHOOSE_AREA = 5
    }

}