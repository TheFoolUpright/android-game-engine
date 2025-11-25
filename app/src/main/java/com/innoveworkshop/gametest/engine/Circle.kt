package com.innoveworkshop.gametest.engine

import android.graphics.Canvas
import android.graphics.Paint

class Circle(x: Float, y: Float, var radius: Float, color: Int) : GameObject(x, y), Caged {
    // Set up the paint.
    var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // PYSHICS HAPPENNING
    var mass:Float = 1f
    var velocity = Vector(0f, 0f)
    var acceleration = Vector(0f, 0f)


    init {
        paint.color = color
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawCircle(position.x, position.y, radius, paint)
    }

    override fun hitLeftWall(): Boolean {
        return (position.x - radius) <= 0
    }

    override fun hitRightWall(): Boolean {
        return (position.x + radius) >= gameSurface!!.width
    }

    override val isFloored: Boolean
        get() = (position.y + radius) >= gameSurface!!.height


    fun applyForce(force: Vector) {
        // F = m * a  →  a = F / m
        acceleration.x += force.x / mass
        acceleration.y += force.y / mass
    }

    fun updatePhysics() {
        // Update velocity from acceleration
        velocity.x += acceleration.x
        velocity.y += acceleration.y

        // Update position from velocity
        position.x += velocity.x
        position.y += velocity.y

        // Reset acceleration
        acceleration.x = 0f
        acceleration.y = 0f
    }
}
