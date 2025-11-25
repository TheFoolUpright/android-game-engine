package com.innoveworkshop.gametest.engine

import android.graphics.Canvas
import android.graphics.Paint

class Circle(x: Float, y: Float, var radius: Float, color: Int) : GameObject(x, y), Caged {
    // Set up the paint.
    var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mass: Int = 1

    init {
        paint.color = color
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawCircle(position.x, position.y, radius, paint)
    }

    override fun hitLeftWall(): Boolean {
        return (position.x - radius) <= gameSurface!!.width
    }

    override fun hitRightWall(): Boolean {
        return (position.x + radius) >= gameSurface!!.width
    }

    override val isFloored: Boolean
        get() = (position.y + radius) >= gameSurface!!.height
}
