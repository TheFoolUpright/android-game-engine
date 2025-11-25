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

            surface.addGameObject(
                Rectangle(
                    Vector((surface.width / 3).toFloat(), (surface.height / 3).toFloat()),
                    200f, 100f, Color.GREEN
                )
            )

            surface.addGameObject(
                DroppingRectangle(
                    Vector((surface.width / 3).toFloat(), (surface.height / 3).toFloat()),
                    100f, 100f, 10f, Color.rgb(128, 14, 80)
                )
            )
        }

        override fun onFixedUpdate() {
            super.onFixedUpdate()

            circle!!.setPosition(
                circle!!.position.x + deviceRotation.roll,
                circle!!.position.y - deviceRotation.pitch
            )
        }
    }
}