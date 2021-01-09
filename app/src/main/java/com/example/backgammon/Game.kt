package com.example.backgammon

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.Collections.max
import java.util.Collections.min
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.properties.Delegates
import kotlin.random.Random
import kotlin.reflect.KFunction2


data class Game(
        var context: Context,
        var players: Array<Player>,
        var areas: HashMap<Int, Area>,
        var dices: Array<ImageView>,
        var diceBoxes: Array<FlexboxLayout>,
        var diceBox: LinearLayout,
        var scoreBoxes: Array<LinearLayout>,
        var bandElement: FlexboxLayout,
        val finish: KFunction2<Array<Player>, Int, Unit>,
        var timerElement: TextView
) {
    // game board
    var house = Array<ArrayList<Pawn>>(2) { _ -> ArrayList<Pawn>() }
    var state:Int = ROLL_DICE_CHOOSE_FIRST_PLAYER
    var currentPlayer:Int = 0 // first player begins
    var possibleMoves:HashMap<Int, ArrayList<Int>> = hashMapOf()
    var sourceAreaID:Int = 0
    var band :ArrayList<Pawn> = arrayListOf()
    var pawnsPerPlayer:Int = 0
    var timeStarted by Delegates.notNull<Long>()


    @RequiresApi(Build.VERSION_CODES.N)
    fun setCurrentState(s: Int) {
        Log.i("bacgammon_debug", "setCurrentState(${s})")
        if (s == ROLL_DICE || s == ROLL_DICE_CHOOSE_FIRST_PLAYER) {
            this.unhideDices()
        }
        if (this.state == ROLL_DICE or ROLL_DICE_CHOOSE_FIRST_PLAYER) {
            this.hideDices()
        }

        this.state = s

        // check if can make any move
        if (this.state == WAIT_TO_CHOOSE_PAWN) {
            if (!this.canMakeAnyMove()) {
                this.nextPlayer()
                this.setCurrentState(ROLL_DICE)
                Toast.makeText(context, "Can't make any move! Now ${this.getCurrentPlayer().getUsername()} plays!", Toast.LENGTH_SHORT).show()
            }
        }

        // AI MODE
        if (this.getCurrentPlayer().isAI()) {
            if (this.state == PAWN_IN_BAND_CLICK_ON_PAWN) {
                GlobalScope.launch {
                    delay(150)
                    Handler(Looper.getMainLooper()).post {
                        this@Game.bandOnClick()
                    }
                }
            }
            if (this.state == ROLL_DICE_CHOOSE_FIRST_PLAYER || this.state == ROLL_DICE) {
                GlobalScope.launch {
                    delay(150)
                    Handler(Looper.getMainLooper()).post {
                        this@Game.diceBoxOnClick()
                    }
                }

            }

            if (this.state == WAIT_TO_CHOOSE_PAWN) {
                val diceset: MutableSet<ArrayList<Int>> = this.getCurrentPlayer().diceset
                this.areas.forEach { (areaID, area) ->
                    if (this.generatePossibleMoves(diceset, areaID).size > 0) {
                        GlobalScope.launch {
                            delay(150)
                            Handler(Looper.getMainLooper()).post {
                                this@Game.areaOnClick(area.element)
                            }
                        }
                    }
                }
            }

            if (this.state == PAWN_CHOSEN_CHOOSE_AREA || this.state == PAWN_IN_BAND_CHOOSE_AREA) {
                GlobalScope.launch {
                    delay(150)
                    Handler(Looper.getMainLooper()).post {
                        this@Game.areaOnClick(this@Game.areas[this@Game.possibleMoves.keys.random()]!!.element)
                    }
                }
            }
        }
    }

    fun getCurrentPlayer(): Player {
        return this.players[this.currentPlayer]
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun nextPlayer() {
        this.currentPlayer = this.currentPlayer.xor(1) // ${} xor 1 = !${}
        Log.i("currentPlayer", this.getCurrentPlayer().getUsername())
        // highlight current player in score boxes
        this.players.forEachIndexed { index, _ ->
            val color: Int = Color.parseColor(if (index == this.currentPlayer) "#ffffff" else "#cccccc")
            for(childID in 0 until scoreBoxes[index].childCount) {
                var tv_scoreBoxText: TextView = scoreBoxes[index].getChildAt(childID) as TextView
                tv_scoreBoxText.setTextColor(color)
            }
        }
        if (this.state == ROLL_DICE_CHOOSE_FIRST_PLAYER && this.getCurrentPlayer().isAI()) {
            GlobalScope.launch {
                delay(150)
                Handler(Looper.getMainLooper()).post {
                    this@Game.diceBoxOnClick()
                }
            }
        }
    }

    fun rollDice(doubleIfSame: Boolean = true): ArrayList<Int> {
        val randomValues = List<Int>(2) { Random.nextInt(1, 6)} as ArrayList<Int>
        if (doubleIfSame && randomValues[0] == randomValues[1]) { // if two same numbers rolled, double
            randomValues += randomValues
        }
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


    private fun generatePossibleMoves(ds:MutableSet<ArrayList<Int>>, areaID:Int) :HashMap<Int, ArrayList<Int>> {
        val pm:HashMap<Int, ArrayList<Int>> = hashMapOf()
        ds.forEach { diceset ->
            val candidateAreaID = diceset.sum() * this.getCurrentPlayer().direction + areaID
            if (candidateAreaID in this.areas.keys) {
                if (this.areas[candidateAreaID]!!.canLetPawn(this.getCurrentPlayer())) {
                    pm[candidateAreaID] = diceset
                }
            }
        }
        Log.i("possibleMoves", "using diceset ${ds.toString()} at areaID ${areaID} = ${pm.toString()}")
        return pm
    }

    fun hideDices() {
        this.diceBox.visibility = View.INVISIBLE
    }

    fun unhideDices() {
        this.diceBox.visibility = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun canMakeAnyMove(): Boolean {
        Log.i("can_make_any_move", "invoked")
        val diceset: MutableSet<ArrayList<Int>> = this.getCurrentPlayer().diceset
        this.areas.forEach { (areaID, area) ->
            if (area.getOwner() == this.getCurrentPlayer()) {
                if (this.generatePossibleMoves(diceset, areaID).size > 0) {
                    Log.i("can_make_any_move", "true, ${this.generatePossibleMoves(diceset, areaID).toString()}")
                    return true
                }
            }
        }
        Log.i("can_make_any_move", "false")
        return false
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun diceBoxOnClick() {
        Log.i("clickEvent", "diceBoxOnClick from ${this.getCurrentPlayer().getUsername()} at state ${this.state}")
        if (this.state == ROLL_DICE_CHOOSE_FIRST_PLAYER) {
            Log.i("dices_rolled", this.getCurrentPlayer().dices.toString())
            if (this.players[0].dices[0] * this.players[1].dices[0] == 0) { // check if any of players didn't rolled dices
                this.getCurrentPlayer().dices = this.rollDice(doubleIfSame = false)
                Log.i("dices_rolled_after", this.getCurrentPlayer().dices.toString())
            }
            Log.i("check_dices", (this.players[0].dices[0] * this.players[1].dices[0]).toString())
            if (this.players[0].dices[0] * this.players[1].dices[0] != 0) { // all players rolled dice, choose who's first
                if (this.players[0].dices.sum() > this.players[1].dices.sum()) { // if player 1 wins, change to next player
                    this.nextPlayer()
                    this.setCurrentState(ROLL_DICE)
                } else {
                    this.setCurrentState(ROLL_DICE)
                }
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

    fun checkIfWin():Boolean {
        val player:Player = this.getCurrentPlayer()
        val areaID = if (player.direction == 1) max(this.areas.keys) else min(this.areas.keys)
        Log.i("checkIfWin", "${this.areas[areaID]!!.getSize()} (areaID ${areaID} == ${this.pawnsPerPlayer}(pawnsPerPlayer) for player ${this.getCurrentPlayer().getUsername()}")
        if (this.areas[areaID]!!.getSize() == this.pawnsPerPlayer) {
            return true
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun areaOnClick(elem:View?) {
        var tag:Int? = (elem!!.tag as String?)?.toIntOrNull()
        val clickedAreaID:Int = tag ?: this.context.resources.getResourceEntryName(elem.id).filter { it.isDigit() }.toInt()
        val clickedArea = this.areas[clickedAreaID]

        // CHOOSE PAWN TO MOVE
        if (this.state == WAIT_TO_CHOOSE_PAWN) {
            if (clickedArea!!.getOwner() == this.getCurrentPlayer()) { // allow to select only own pawns
                this.possibleMoves = this.generatePossibleMoves(this.getCurrentPlayer().diceset, clickedAreaID)
                this.possibleMoves.forEach { areaID, _ ->
                    this.areas[areaID]!!.highlight()
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
                val pawn:Pawn = this.areas[this.sourceAreaID]!!.pop()!!
                pawn.unHighlight()
                if (clickedAreaID in arrayOf(-1, 24)) {
                    pawn.player.addScore(30)
                } else {
                    pawn.player.addScore(10)
                }
                val leftOverPawn:Pawn? = clickedArea!!.addPawn(pawn)
                // add leftover pawn to the band
                if (leftOverPawn != null) {
                    leftOverPawn.player.addScore(-50)
                    this.addToBand(leftOverPawn)
                }
                for (key in this.possibleMoves.keys) {
                    this.areas[key]!!.unHighlight()
                }
                this.possibleMoves[clickedAreaID]?.forEach { diceNumber ->
                    this.getCurrentPlayer().dices.remove(diceNumber)
                }
                if (this.checkIfWin()) {
                    this.finish(this.players, this.currentPlayer)
                }
                if (this.getCurrentPlayer().dices.size > 0) {
                    this.setCurrentState(WAIT_TO_CHOOSE_PAWN)
                } else {
                    this.nextPlayer()
                    this.setCurrentState(ROLL_DICE)
                }
            }
        }

        if (this.state == PAWN_IN_BAND_CHOOSE_AREA) {
            if (clickedAreaID in this.possibleMoves) {
                var pawn:Pawn = this.popFromBand()!!
                pawn.unHighlight()
                var leftOverPawn:Pawn? = clickedArea!!.addPawn(pawn)
                if (leftOverPawn != null) {
                    leftOverPawn.player.addScore(-50)
                    this.addToBand(leftOverPawn)
                }
                for (key in this.possibleMoves.keys) {
                    this.areas[key]!!.unHighlight()
                }
                this.possibleMoves[clickedAreaID]?.forEach { diceNumber ->
                    this.getCurrentPlayer().dices.remove(diceNumber)
                }
                if (this.getCurrentPlayer().dices.size > 0) {
                    this.setCurrentState(WAIT_TO_CHOOSE_PAWN)
                } else {
                    this.nextPlayer()
                    this.setCurrentState(ROLL_DICE)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun bandOnClick() {
        if (this.state == PAWN_IN_BAND_CLICK_ON_PAWN) {
            val pawn:Pawn = this.getLastPawnInBand()!!
            pawn.highlight()
            val player:Player = this.getCurrentPlayer()
            val startArea = if (player.direction == 1) -1 else this.areas.size
            this.possibleMoves = this.generatePossibleMoves(this.getCurrentPlayer().diceset, startArea)
            if (this.possibleMoves.size == 0) { // cant move pawn, skip turn
                this.nextPlayer()
                this.setCurrentState(ROLL_DICE)
            }
            this.possibleMoves.forEach { areaID, _ ->
                this.areas[areaID]!!.highlight()
            }
            this.setCurrentState(PAWN_IN_BAND_CHOOSE_AREA)
        }
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
        this.pawnsPerPlayer = initialPivotPositions[0].map { e -> e[1] }.sum()
        Log.i("pawns_per_player", this.pawnsPerPlayer.toString())
        initialPivotPositions.forEachIndexed { playerID, pivotPositions ->
            for (pos in pivotPositions) {
                repeat(pos[1]) {
                    var pawn = Pawn(this.context, this.players[playerID])
                    this.areas[pos[0]]!!.addPawn(pawn)
                    // https://stackoverflow.com/questions/44874843/remove-imageview-programmatically-from-custom-layout
                    // +
                    // https://stackoverflow.com/questions/15097950/adding-imageview-to-the-layout-programmatically
                }
            }
        }

        // attachListeners
        this.diceBox.setOnClickListener {
            if (!this.getCurrentPlayer().isAI()) {
                this.diceBoxOnClick()
            }
        }



        this.areas.forEach { _, area ->
            area.element.setOnClickListener { elem ->
                if (!this.getCurrentPlayer().isAI()) {
                    this.areaOnClick(elem)
                }
            }
        }

        this.bandElement.setOnClickListener {
            if (!this.getCurrentPlayer().isAI()) {
                this.bandOnClick()
            }
        }
        this.timeStarted = Calendar.getInstance().timeInMillis

        Handler(Looper.getMainLooper()).post(object : Runnable {
            override fun run() {
                updateTimer()
                Handler(Looper.getMainLooper()).postDelayed(this, 1000)
            }
        })
    }

    fun updateTimer() {
        var seconds_passed:Long = (Calendar.getInstance().timeInMillis - this.timeStarted)/1000
        this.timerElement.text = "${(seconds_passed / 60).toLong()}:${(seconds_passed%60).toString().padStart(2, '0')}"
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