package com.example.backgammon

import android.content.Context
import android.util.Log
import com.google.android.flexbox.FlexboxLayout

data class Game(var context: Context, var players: Array<Player>, var areas: ArrayList<FlexboxLayout>) {
    // game board
    var board = Array<ArrayList<Pawn>>(24) { i -> ArrayList<Pawn>() }
    var house = Array<ArrayList<Pawn>>(2) { i -> ArrayList<Pawn>() }


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
    }

    companion object {
        const val ROLL_DICE_CHOOSE_FIRST_PLAYER = 0
        const val ROLL_DICE = 1
        const val WAIT_TO_CHOOSE_PAWN = 2
        const val PAWN_CHOSEN_CHOOSE_AREA = 3
    }

}