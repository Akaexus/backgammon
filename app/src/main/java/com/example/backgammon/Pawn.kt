package com.example.backgammon

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayout.LayoutParams


class Pawn {
    var player:Player
    var color: String
    var element: ImageView
    lateinit var originalDrawable: Drawable
    var context:Context
    var highlighted:Boolean = false
    var mode:String = "original"
    val drawableIDs:HashMap<String, HashMap<String, Int>> = hashMapOf(
            "original" to hashMapOf(
                    "red" to R.drawable.pawn_red,
                    "blue" to R.drawable.pawn_blue,
                    "highlight" to R.drawable.pawn_chosen,
            ),
            "vivid" to hashMapOf(
                    "red" to R.drawable.pawn2_red,
                    "blue" to R.drawable.pawn2_blue,
                    "highlight" to R.drawable.pawn2_chosen,
            ),
    )

    constructor(context: Context, player: Player) {
        this.context = context
        this.player = player
        this.color = this.player.color
        this.element = ImageView(context)
        val size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, context.resources.getDisplayMetrics()).toInt()
        this.element.layoutParams = FlexboxLayout.LayoutParams(size, size)
        this.setMode("original")
    }

    fun setMode(m:String):Boolean {
        if (m in drawableIDs) {
            this.mode = m
            this.originalDrawable = context.getDrawable(this.drawableIDs[mode]?.get(color)!!)!!
            this.element.setImageDrawable(originalDrawable)
        }
        return false
    }

    fun switchMode() {
        if (this.mode == "vivid") {
            this.setMode("original")
        } else {
            this.setMode("vivid")
        }
    }

    fun highlight() {
        this.highlighted = true
        this.element.setImageDrawable(context.getDrawable(this.drawableIDs[mode]?.get("highlight")!!))
    }

    fun unHighlight() {
        this.highlighted = false
        this.element.setImageDrawable(this.originalDrawable)
    }

}