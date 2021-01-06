package com.example.backgammon

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayout.LayoutParams


class Pawn {
    var player:Player
    var color: String
    var element: ImageView
    constructor(context: Context, player: Player) {
        this.player = player
        this.color = this.player.color
        this.element = ImageView(context)
        val size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, context.resources.getDisplayMetrics()).toInt()
        this.element.layoutParams = FlexboxLayout.LayoutParams(size, size)
        this.element.setImageDrawable(context.getDrawable(if (this.color == "red") R.drawable.pawn_red else R.drawable.pawn_blue))
    }
}