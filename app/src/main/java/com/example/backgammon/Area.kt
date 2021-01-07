package com.example.backgammon

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.flexbox.FlexboxLayout

class Area {
    var element: FlexboxLayout
    var pawns:ArrayList<Pawn> = arrayListOf()
    private var image:ImageView
    private var originalDrawable: Drawable
    private var highlightDrawable: Drawable

    constructor(element: FlexboxLayout, context: Context) {
        this.element = element
        this.image = (element.parent as ViewGroup).getChildAt(0) as ImageView
        this.originalDrawable = this.image.drawable
        this.highlightDrawable = context.getDrawable(R.drawable.triangle_green)!!
    }

    fun highlight() {
        this.image.setImageDrawable(this.highlightDrawable)
    }

    fun unHighlight() {
        this.image.setImageDrawable(this.originalDrawable)
    }

    fun addPawn(pawn: Pawn) {
        this.pawns.add(pawn)
        this.element.addView(pawn.element)
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
        if (this.getSize() > 1 && pawn.player != this.getOwner()) {
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
}