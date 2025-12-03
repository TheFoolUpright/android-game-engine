package com.innoveworkshop.gametest.assets

import com.innoveworkshop.gametest.engine.Circle
import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

class PitZone(
    position: Vector?,
    width: Float,
    height: Float,
    color: Int
) : Rectangle(position, width, height, color) {

    // Edges of the pit rectangle
    fun leftEdge()   = position.x - width / 2
    fun rightEdge()  = position.x + width / 2
    fun topEdge()    = position.y - height / 2
    fun bottomEdge() = position.y + height / 2

    // Whether the pit can currently trigger a fall
    var isEnabled = true

    /**
     * Checks whether the circle's center of mass enters the pit area.
     *
     * @param circle The falling circle object.
     * @param onFall A callback invoked when the circle falls into the pit.
     */
    fun detectCircleFall(circle: Circle, onFall: () -> Unit) {
        if (!isEnabled) return

        val circleX = circle.position.x
        val circleY = circle.position.y

        val isInside =
            circleX in leftEdge()..rightEdge() &&
                    circleY in topEdge()..bottomEdge()

        if (isInside) {
            isEnabled = false
            onFall()
        }
    }
}
