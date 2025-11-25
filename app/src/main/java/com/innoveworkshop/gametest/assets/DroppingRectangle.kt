package com.innoveworkshop.gametest.assets

import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

class DroppingRectangle(
    position: Vector?,
    width: Float,
    height: Float,
    dropRate: Float,
    color: Int
) : Rectangle(position, width, height, color) {
    var dropRate: Float = 0f
    var mass = 1

    init {
        this.dropRate = dropRate
    }

    override fun onFixedUpdate() {
        super.onFixedUpdate()

        if (!isFloored) position.y += dropRate
    }
}
