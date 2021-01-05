package com.example.backgammon

data class Player (val user: User?, var color: String) {
    var score = 0

    fun addScore(points: Int) {
        this.score += points
    }
}