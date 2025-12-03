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
import androidx.appcompat.app.AppCompatActivity
import com.innoveworkshop.gametest.assets.DeviceRotation
import com.innoveworkshop.gametest.assets.PitZone
import com.innoveworkshop.gametest.assets.WallRectangle
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
        //Log.i("GyroDataEuler", deviceRotation.toString())
        deviceRotation.fromSensorEventQuaternion(data, rateMagnitude)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    inner class Game : GameObject() {
        var circle: Circle? = null
        var originalRadius = 0f
        var isFalling = false
        var fallProgress = 1.0f
        val fallSpeed = 0.05f

        var startRect: Rectangle? = null
        lateinit var startPos: Vector
        var goalRect: Rectangle? = null
        var fallingPit: PitZone? = null
        var pitCenter: Vector? = null



        val mazeLayout = arrayOf(
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1),
            intArrayOf(1,0,0,0,0,0,2,1,0,0,0,0,1,0,0,0,2,2,0,0,0,0,1),
            intArrayOf(1,0,0,0,0,0,0,1,0,1,0,0,1,0,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,0,1,1,1,1,0,1,0,1,0,2,1,0,2,1,1,1,1,1,1,1,1),
            intArrayOf(1,0,0,0,0,1,0,1,0,1,0,2,1,0,0,0,0,0,0,0,0,0,1),
            intArrayOf(1,1,1,1,0,1,0,1,0,1,0,0,1,1,1,1,1,1,1,1,1,0,1),
            intArrayOf(1,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,2,2,0,1),
            intArrayOf(1,0,0,0,0,1,2,1,0,1,0,0,0,0,2,2,0,0,0,0,0,0,1),
            intArrayOf(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1),
        )
        var wallSizeX = 100f
        var wallSizeY = 100f
        // Epsilon - fudge factor
        val epsilon = .5f
        val walls = mutableListOf<WallRectangle>()
        val pits = mutableListOf<PitZone>()


        override fun onStart(surface: GameSurface?) {
            super.onStart(surface)

            wallSizeX = gameSurface!!.width / mazeLayout[0].size.toFloat()
            wallSizeY = gameSurface!!.height / mazeLayout.size.toFloat()


            //Import the maze
            for (row in mazeLayout.indices) {
                for (col in mazeLayout[row].indices) {
                    if (mazeLayout[row][col] == 1) {
                        val x = col * wallSizeX + wallSizeX / 2
                        val y = row * wallSizeY + wallSizeY / 2

                        val wall = WallRectangle(
                            position = Vector(x, y),
                            width = wallSizeX + epsilon,
                            height = wallSizeY + epsilon,
                            color = Color.rgb(102, 51, 0)
                        )

                        walls.add(wall)
                        surface!!.addGameObject(wall)
                    }
                    if (mazeLayout[row][col] == 2) {
                        val x = col * wallSizeX + wallSizeX / 2
                        val y = row * wallSizeY + wallSizeY / 2

                        val pit = PitZone(
                            position = Vector(x, y),
                            width = wallSizeX + epsilon,
                            height = wallSizeY + epsilon,
                            color = Color.BLACK
                        )

                        pits.add(pit)
                        surface!!.addGameObject(pit)
                    }
                }
            }
            //Make the start and end goals
            val startCell = Pair(7, 1)      // row=8, col=1
            val goalCell = Pair(2, 21)

            val startX = startCell.second * wallSizeX + wallSizeX / 2
            val startY = startCell.first * wallSizeY

            startRect = Rectangle(
                position = Vector(startX, startY),
                width = wallSizeX + epsilon,
                height = wallSizeY+wallSizeY + epsilon,
                color = Color.GREEN
            )
            surface!!.addGameObject(startRect!!)

            val goalX = goalCell.second * wallSizeX + wallSizeX / 2
            val goalY = goalCell.first * wallSizeY

            goalRect = Rectangle(
                position = Vector(goalX, goalY),
                width = wallSizeX + epsilon,
                height = wallSizeY + wallSizeY + epsilon,
                color = Color.RED
            )
            surface.addGameObject(goalRect!!)

            //Add Player circle
            circle = Circle(
                startX,
                startY,
                Math.min(wallSizeX, wallSizeY)/2,
                Color.DKGRAY
            )
            surface.addGameObject(circle!!)

            originalRadius = circle!!.radius
            startPos = Vector(startX, startY)
        }

        override fun onFixedUpdate() {
            super.onFixedUpdate()

            val circle = circle ?: return

            //Animate the falling of the ball
            if (isFalling && pitCenter != null) {
                fallProgress -= fallSpeed
                if (fallProgress < 0f) fallProgress = 0f

                // Shrink radius
                circle.radius = originalRadius * fallProgress

                // Move toward pit center
                circle.position.x += (pitCenter!!.x - circle.position.x) * fallProgress * 0.2f
                circle.position.y += (pitCenter!!.y - circle.position.y) * fallProgress * 0.2f

                // Finished falling
                if (fallProgress <= 0f) {
                    // Reset circle
                    circle.position = Vector(startPos.x, startPos.y)
                    circle.velocity.x  = 0f
                    circle.velocity.y  = 0f
                    circle.radius = originalRadius

                    // Reset pit
                    fallingPit?.isEnabled = true

                    // Clear fall state
                    isFalling = false
                    fallingPit = null
                    pitCenter = null
                }

                return  // skip other updates while falling
            }


            // Convert deviceRotation to a force
            val forceX = deviceRotation.roll * 0.5f
            val forceY = -deviceRotation.pitch * 0.5f

            circle.applyForce(Vector(forceX, forceY))

            // Update physics each frame
            circle.updatePhysics()

            // Clamp to boundaries
            keepCircleInside(circle)

            // Wall collisions
            for (wall in walls) {
                wall.checkCollisionCircle(circle)
            }

            //Pit collisions
            for (pit in pits) {
                pit.detectCircleFall(circle){
                    if (!isFalling) {
                        isFalling = true
                        fallProgress = 1.0f
                        fallingPit = pit
                        pitCenter = Vector(pit.position.x, pit.position.y)  // center of the pit
                        pit.isEnabled = false
                    }
                }
            }

            //End Game
            checkGoalReached(circle)
        }

        fun checkGoalReached(circle: Circle) {
            val goal = goalRect ?: return

            val dx = circle.position.x - goal.position.x
            val dy = circle.position.y - goal.position.y
            val distanceX = Math.abs(dx)
            val distanceY = Math.abs(dy)


            if (distanceX < goal.width / 2 && distanceY < goal.height / 2) {
                Log.i("Game", "🎉 Goal reached!")
                circle.position = Vector(startPos.x, startPos.y)
                circle.velocity.x  = 0f
                circle.velocity.y  = 0f
            }
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