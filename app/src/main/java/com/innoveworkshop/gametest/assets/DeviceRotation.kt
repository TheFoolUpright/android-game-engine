package com.innoveworkshop.gametest.assets

import android.hardware.SensorEvent
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Abstracts away the quaternion mess from the gyroscope into something
 * we can understand.
 */
class DeviceRotation {
    public var roll: Float = 0f
    public var pitch: Float = 0f
    public var yaw: Float = 0f

    /**
     * Convert a sensor event data quaternion to a device rotation.
     *
     * @param data Sensor event data.
     * @param magnitude Multiplier factor to give it more umph.
     */
    fun fromSensorEventQuaternion(data: SensorEvent, magnitude: Float) {
        // Get quaternion values from sensor event data.
        val x = data.values[0]
        val y = data.values[1]
        val z = data.values[2]
        val w = data.values[3]

        // roll (x-axis rotation)
        val sinr_cosp: Double = (2 * (w * x + y * z)) * 1.0
        val cosr_cosp: Double = (1 - 2 * (x * x + y * y)) * 1.0
        roll =  atan2(sinr_cosp, cosr_cosp).toFloat() * magnitude

        // pitch (y-axis rotation)
        val sinp: Double = sqrt(1 + 2 * (w * y - x * z)) * 1.0
        val cosp: Double = sqrt(1 - 2 * (w * y - x * z)) * 1.0
        pitch = (2 * atan2(sinp, cosp) - PI / 2).toFloat() * magnitude

        // yaw (z-axis rotation)
        val siny_cosp: Double = 2 * (w * z + x * y) * 1.0
        val cosy_cosp: Double = 1 - 2 * (y * y + z * z) * 1.0
        yaw = atan2(siny_cosp, cosy_cosp).toFloat() * magnitude
    }

    override fun toString(): String {
        return "roll = $roll\tpitch = $pitch\tyaw = $yaw"
    }
}