package chapter7

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array

class Plane(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    init {
        val fileNames: Array<String> = Array()
        fileNames.add("assets/planeGreen0.png")
        fileNames.add("assets/planeGreen1.png")
        fileNames.add("assets/planeGreen2.png")
        fileNames.add("assets/planeGreen1.png")
        loadAnimationFromFiles(fileNames, .1f, true)

        setMaxSpeed(800f)
        setBoundaryPolygon(8)
    }

    override fun act(dt: Float) {
        super.act(dt)

        // simulate force of gravity
        setAcceleration(800f)
        accelerateAtAngle(270f)
        applyPhysics(dt)

        // stop plane from passing through the ground
        for (ground: BaseActor in BaseActor.getList(this.stage, Ground::class.java.canonicalName)) {
            if (this.overlaps(ground)) {
                setSpeed(0f)
                preventOverlap(ground)
            }
        }

        // stop plane from moving past the top of the screen
        if (y + height > getWorldBounds().height) {
            setSpeed(0f)
            boundToWorld()
        }

    }

    fun boost() {
        setSpeed(300f)
        setMotionAngle(90f)
    }
}