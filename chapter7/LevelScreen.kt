package chapter7

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Label

class LevelScreen : BaseScreen() {
    private lateinit var plane: Plane

    private var starTimer = 0f
    private var starSpawnInterval = 4f

    private var score = 0
    private lateinit var scoreLabel: Label

    private var enemyTimer = 0f
    private var enemySpeed = 100f
    private var enemySpawnInterval = 3f

    private var gameOver = false
    private lateinit var gameOverMessage: BaseActor

    override fun initialize() {
        Sky(0f, 0f, mainStage)
        Sky(800f, 0f, mainStage)
        Ground(0f, 0f, mainStage)
        Ground(800f, 0f, mainStage)

        plane = Plane(100f, 500f, mainStage)
        BaseActor.setWorldBounds(800f, 600f)

        scoreLabel = Label("$score", BaseGame.labelStyle)

        gameOverMessage = BaseActor(0f, 0f, mainStage)
        gameOverMessage.loadTexture("assets/game-over.png")
        gameOverMessage.isVisible = false

        uiTable.pad(10f)
        uiTable.add(scoreLabel)
        uiTable.row()
        uiTable.add(gameOverMessage).expandY()
    }

    override fun update(dt: Float) {
        if (gameOver)
            return

        starTimer += dt
        if (starTimer > starSpawnInterval) {
            Stars(800f, MathUtils.random(100f, 500f), mainStage)
            starTimer = 0f
        }

        for (star: BaseActor in BaseActor.getList(mainStage, Stars::class.java.canonicalName)) {
            if (plane.overlaps(star)) {
                star.remove()
                score++
                scoreLabel.setText("$score")
            }
        }

        enemyTimer += dt
        if (enemyTimer > enemySpawnInterval) {
            val enemy = Enemy(800f, MathUtils.random(100f, 500f), mainStage)
            enemy.setSpeed(enemySpeed)

            enemyTimer = 0f
            enemySpawnInterval -= .1f
            enemySpeed += 10

            if (enemySpawnInterval < .5f)
                enemySpawnInterval = .5f

            if (enemySpeed > 400f)
                enemySpeed = 400f
        }

        for (enemy: BaseActor in BaseActor.getList(mainStage, Enemy::class.java.canonicalName)) {
            if (plane.overlaps(enemy)) {
                plane.remove()
                gameOver = true
                gameOverMessage.isVisible = true
            }

            if (enemy.x + enemy.width < 0) {
                score++
                scoreLabel.setText("$score")
                enemy.remove()
            }
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.SPACE) {
            plane.boost()
        }

        return false
    }
}
