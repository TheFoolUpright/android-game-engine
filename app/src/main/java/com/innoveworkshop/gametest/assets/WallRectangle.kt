package com.innoveworkshop.gametest.assets

import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

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



}