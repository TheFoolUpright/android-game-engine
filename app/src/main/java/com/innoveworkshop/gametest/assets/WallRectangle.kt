package com.innoveworkshop.gametest.assets

import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector
import com.innoveworkshop.gametest.engine.Circle

class WallRectangle(
    position: Vector?,
    width: Float,
    height: Float,
    color: Int
) : Rectangle(position, width, height, color)  {
    var left: Float = position!!.x - width / 2
    var right: Float = position!!.x + width / 2
    var top: Float = position!!.y - height / 2
    var bottom: Float = position!!.y + height / 2
    var friction: Float = 0.90f

    init {
        left = position!!.x - width / 2
        right = position!!.x + width / 2
        top = position!!.y - height / 2
        bottom = position!!.y + height / 2
    }

    fun updateBounds() {
        left = position.x - width / 2
        right = position.x + width / 2
        top = position.y - height / 2
        bottom = position.y + height / 2
    }

    fun checkCollisionCircle(circle: Circle) {
        // Update rectangle boundary edges
        updateBounds()

        // Get the circle center
        val circleX = circle.position.x
        val circleY = circle.position.y

        // Closest point on the rectangle to the circle
        val nearestX = clamp(circleX, left, right)
        val nearestY = clamp(circleY, top, bottom)

        // Vector from nearest point to circle center
        val vectorToCircleX = circleX - nearestX
        val vectorToCircleY = circleY - nearestY

        // Squared distance from circle center to nearest point
        val distanceSquared = vectorToCircleX * vectorToCircleX +
                vectorToCircleY * vectorToCircleY

        val circleRadius = circle.radius
        val circleRadiusSquared = circleRadius * circleRadius

        // If the circle center lies exactly on the rectangle boundary
        if (distanceSquared == 0f) {
            circle.position.y = top - circleRadius - 0.1f
            circle.velocity.y = 0f
            return
        }

        // If the circle and rectangle collide
        if (distanceSquared < circleRadiusSquared) {

            val distance = kotlin.math.sqrt(distanceSquared)
            val penetrationDepth = circleRadius - distance

            // Collision normal (unit vector pushing the circle out)
            val normalX = vectorToCircleX / distance
            val normalY = vectorToCircleY / distance

            // Push circle out of the wall
            circle.position.x += normalX * penetrationDepth
            circle.position.y += normalY * penetrationDepth

            // Remove the inward component of the velocity
            val velocityInNormalDirection =
                circle.velocity.x * normalX + circle.velocity.y * normalY

            if (velocityInNormalDirection < 0f) {
                circle.velocity.x -= velocityInNormalDirection * normalX
                circle.velocity.y -= velocityInNormalDirection * normalY
            }

            // Tangent vector (perpendicular to the normal)
            val tangentX = -normalY
            val tangentY = normalX

            // Circle velocity in tangent direction
            val velocityInTangentDirection =
                circle.velocity.x * tangentX + circle.velocity.y * tangentY

            val slowedTangentVelocity = velocityInTangentDirection * friction

            // Reconstruct velocity using normal + tangent components
            circle.velocity.x =
                (circle.velocity.x - velocityInTangentDirection * tangentX) +
                        slowedTangentVelocity * tangentX

            circle.velocity.y =
                (circle.velocity.y - velocityInTangentDirection * tangentY) +
                        slowedTangentVelocity * tangentY
        }
    }



    // helper function to clamp a value between a minimum and a maximum
    private fun clamp(value: Float, min: Float, max: Float): Float {
        return kotlin.math.max(min, kotlin.math.min(max, value))
    }



}