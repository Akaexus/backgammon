package com.example.backgammon

import android.util.Log

data class Player (val user: User?, var color: String) {
    var score = 0
    var direction:Int = 1 // 1 = clockwise, -1 = clockwise
    var dices:ArrayList<Int> = arrayListOf(0, 0)
        set(value) {
            field = value
        }
    var diceset :MutableSet<ArrayList<Int>> = mutableSetOf()
        get() = this.generateDiceset()

    private fun generateDiceset(): MutableSet<ArrayList<Int>> {
        var movesSet:MutableSet<ArrayList<Int>> = mutableSetOf()
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
                movesSet.add(moves)
            }
        }
        Log.i("possibleMoves", movesSet.toString())
        return movesSet
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