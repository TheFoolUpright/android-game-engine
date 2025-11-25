package com.innoveworkshop.gametest

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.innoveworkshop.gametest.assets.DeviceRotation
import com.innoveworkshop.gametest.assets.DroppingRectangle
import com.innoveworkshop.gametest.engine.Circle
import com.innoveworkshop.gametest.engine.GameObject
import com.innoveworkshop.gametest.engine.GameSurface
import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

class MainActivity : AppCompatActivity(), SensorEventListener {
    protected var gameSurface: GameSurface? = null

    public var deviceRotation: DeviceRotation = DeviceRotation()
    public var rateMagnitude = 5f

    protected var game: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Initialize gyro.
        initializeGyro()


        gameSurface = findViewById<View>(R.id.gameSurface) as GameSurface
        game = Game()
        gameSurface!!.setRootGameObject(game)

    }

    fun initializeGyro() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        sensorManager.registerListener(this, sensor, 100000)
    }

    override fun onSensorChanged(data: SensorEvent?) {
        if (data == null)
            return

        //Log.i("GyroDataQuaternion", "(${data.values[0]}, ${data.values[1]}, ${data.values[2]}, [${data.values[3]}])")
        Log.i("GyroDataEuler", deviceRotation.toString())
        deviceRotation.fromSensorEventQuaternion(data, rateMagnitude)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    inner class Game : GameObject() {
        var circle: Circle? = null

        override fun onStart(surface: GameSurface?) {
            super.onStart(surface)

            circle = Circle(
                (surface!!.width / 2).toFloat(),
                (surface.height / 2).toFloat(),
                100f,
                Color.RED
            )
            surface.addGameObject(circle!!)



        }

        override fun onFixedUpdate() {
            super.onFixedUpdate()

            val circle = circle ?: return

            // Convert deviceRotation to a force
            val forceX = deviceRotation.roll * 0.5f
            val forceY = -deviceRotation.pitch * 0.5f

            circle.applyForce(Vector(forceX, forceY))

            // Update physics each frame
            circle.updatePhysics()

            // Clamp to boundaries
            keepCircleInside(circle)
        }

        fun keepCircleInside(circle: Circle) {
            val stickiness = 0.90f // Adding friction when touching the wall

            // LEFT WALL
            if(circle.position.x - circle.radius < 0) {
                circle.position.x = circle.radius
                circle.velocity.x *= 0f
                circle.velocity.y *= stickiness
            }

            // RIGHT WALL
            if(circle.position.x + circle.radius > gameSurface!!.width) {
                circle.position.x = gameSurface!!.width - circle.radius
                circle.velocity.x *= 0f
                circle.velocity.y *= stickiness
            }

            // TOP WALL
            if(circle.position.y - circle.radius < 0) {
                circle.position.y = circle.radius
                circle.velocity.y *= 0f
                circle.velocity.x *= stickiness
            }

            // BOTTOM WALL
            if(circle.position.y + circle.radius > gameSurface!!.height) {
                circle.position.y = gameSurface!!.height - circle.radius
                circle.velocity.y *= 0f
                circle.velocity.x *= stickiness
            }
        }
    }
}