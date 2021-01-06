package com.example.backgammon

import android.util.Log

data class Player (val user: User?, var color: String) {
    var score = 0
    var dices:List<Int> = listOf(0, 0)
        set(value) {
            field = value.sorted()
            this.generatePossibleMoves()
        }
    var possibleMoves :MutableSet<ArrayList<Int>> = mutableSetOf()

    private fun generatePossibleMoves() {
        this.possibleMoves = mutableSetOf()
        for (matrix in 0 until Math.pow(2.0, dices.size.toDouble()).toInt()) {
            var moves :ArrayList<Int> = arrayListOf()
            var pattern = matrix
            for (index in dices.indices) {
                if (pattern %2 == 1) {
                    moves.add(dices[index])
                }
                pattern = pattern shr 1
            }
            if (moves.size > 0) {
                moves.sort()
                possibleMoves.add(moves)
            }
        }
        Log.i("possibleMoves", this.possibleMoves.toString())
    }

    fun addScore(points: Int) {
        this.score += points
    }

    fun getUsername(): String {
        if (this.user != null) {
            return this.user.login
        }
        return "AI"
    }



}