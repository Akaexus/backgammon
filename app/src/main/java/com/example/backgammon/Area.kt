package com.example.backgammon

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.flexbox.FlexboxLayout

class Area {
    var element: FlexboxLayout
    var pawns:ArrayList<Pawn> = arrayListOf()
    private lateinit var image:ImageView
    private lateinit var originalDrawable: Drawable
    private lateinit var highlightDrawable: Drawable
    var type = TYPE_NORMAL

    constructor(element: FlexboxLayout, context: Context, t:Int = TYPE_NORMAL) {
        this.element = element
        this.type = t
        if (this.type == TYPE_NORMAL) {
            this.image = (element.parent as ViewGroup).getChildAt(0) as ImageView
            this.originalDrawable = this.image.drawable
            this.highlightDrawable = context.getDrawable(R.drawable.triangle_green)!!
        }
    }

    fun highlight() {
        if (this.type == TYPE_NORMAL) {
            this.image.setImageDrawable(this.highlightDrawable)
        } else {
            this.element.setBackgroundResource(R.drawable.wood2_highlight)
        }
    }

    fun unHighlight() {
        if (this.type == TYPE_NORMAL) {
            this.image.setImageDrawable(this.originalDrawable)
        } else {
            this.element.setBackgroundResource(R.drawable.wood2)
        }
    }

    fun addPawn(pawn: Pawn): Pawn? {
        var leftOverPawn:Pawn? = null
        if (this.getSize() == 1 && this.lastPawn()!!.player != pawn.player) {
             leftOverPawn = this.pop()
        }
        this.pawns.add(pawn)
        this.element.addView(pawn.element)
        return leftOverPawn
    }

    fun getSize(): Int {
        return this.pawns.size
    }

    fun getOwner() :Player? {
        if (this.getSize() == 0) {
            return null
        }
        return this.pawns[0].player
    }

    fun lastPawn(): Pawn? {
        if (this.getSize() > 0) {
            return this.pawns.last()
        }
        return null
    }

    fun canLetPawn(pawn: Pawn): Boolean {
        return this.canLetPawn(pawn.player)
    }

    fun canLetPawn(player: Player): Boolean {
        if (this.getSize() > 1 &&player != this.getOwner()) {
            return false
        }
        return true
    }

    fun pop(): Pawn? {
        if (this.getSize() > 0) {
            this.element.removeViewAt(this.element.childCount - 1)
            val pawn: Pawn = this.lastPawn()!!
            this.pawns.removeLast()
            return pawn
        }
        return null
    }
    companion object {
        const val TYPE_NORMAL = 0
        const val TYPE_DOCK = 1
    }
}