package chapter11

import kotlin.math.abs
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table

class LevelScreen : BaseScreen() {
    private lateinit var jack: Koala

    private var gameOver = false
    private var coins = 0
    private var time = 60f
    private lateinit var coinLabel: Label
    private lateinit var keyTable: Table
    private lateinit var timeLabel: Label
    private lateinit var messageLabel: Label

    override fun initialize() {
        val tma = TilemapActor("assets/map.tmx", mainStage)

        for (obj in tma.getRectangleList("solid")) {
            val props = obj.properties
            Solid(
                props.get("x") as Float,
                props.get("y") as Float,
                props.get("width") as Float,
                props.get("height") as Float,
                mainStage
                )
        }

        val startPoint = tma.getRectangleList("start")[0]
        val startProps = startPoint.properties
        jack = Koala(startProps.get("x") as Float, startProps.get("y") as Float, mainStage)

        coinLabel = Label("Coins: $coins", BaseGame.labelStyle)
        coinLabel.color = Color.GOLD
        keyTable = Table()
        timeLabel = Label("Time: ${time.toInt()}", BaseGame.labelStyle)
        timeLabel.color = Color.LIGHT_GRAY
        messageLabel = Label("Message", BaseGame.labelStyle)
        messageLabel.isVisible = false

        uiTable.pad(20f)
        uiTable.add(coinLabel)
        uiTable.add(keyTable).expandX()
        uiTable.add(timeLabel)
        uiTable.add().row()
        uiTable.add(messageLabel).colspan(3).expandY()
    }

    override fun update(dt: Float) {
        for (actor in BaseActor.getList(mainStage, Solid::class.java.canonicalName)) {
            val solid = actor as Solid

            if (jack.overlaps(solid) && solid.enabled) {
                val offset = jack.preventOverlap(solid)

                if (offset != null) {
                    // collision in X direction
                    if (abs(offset.x) > abs(offset.y))
                        jack.velocityVec.x = 0f
                    else
                        jack.velocityVec.y = 0f
                }
            }
        }
    }

    override fun keyDown(keyCode: Int): Boolean {
        if (keyCode == Keys.SPACE) {
            if (jack.isOnSolid())
                jack.jump()
        }
        return false
    }
}
