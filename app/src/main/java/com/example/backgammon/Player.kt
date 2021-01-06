package com.example.backgammon

data class Player (val user: User?, var color: String) {
    var score = 0
    var dices:List<Int> = listOf(0, 0)

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