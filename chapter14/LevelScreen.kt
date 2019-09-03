package chapter14

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label

class LevelScreen : BaseScreen() {
    private lateinit var maze: Maze
    private lateinit var hero: Hero
    private lateinit var ghost: Ghost

    lateinit var coinsLabel: Label
    lateinit var messageLabel: Label

    lateinit var coinSound: Sound
    lateinit var hurtSound: Sound
    lateinit var winSound: Sound
    lateinit var looseSound: Sound
    lateinit var powerupSound: Sound
    lateinit var windMusic: Music

    override fun initialize() {
        val background = BaseActor(0f, 0f, mainStage)
        background.loadTexture("assets/white.png")
        background.color = Color.GRAY
        background.setSize(768f, 700f)

        maze = Maze(mainStage)

        hero = Hero(0f, 0f, mainStage)
        hero.centerAtActor(maze.getRoom(0, 0)!!)

        ghost = Ghost(0f, 0f, mainStage)
        ghost.centerAtActor(maze.getRoom(11, 9)!!)

        for (room in BaseActor.getList(mainStage, Room::class.java.canonicalName)) {
            if (MathUtils.random() >= .6f) { // P(maze with no Coins) = .4^(10*12) = 1.76*10^-48
                val coin = Coin(0f, 0f, mainStage)
                coin.centerAtActor(room)
            }
        }

        ghost.toFront()

        coinsLabel = Label("Coins left:", BaseGame.labelStyle)
        coinsLabel.color = Color.GOLD
        messageLabel = Label("...", BaseGame.labelStyle)
        messageLabel.setFontScale(2f)
        messageLabel.isVisible = false

        uiTable.pad(10f)
        uiTable.add(coinsLabel)
        uiTable.row()
        uiTable.add(messageLabel).expandY()

        coinSound = Gdx.audio.newSound(Gdx.files.internal("assets/coin.wav"))
        hurtSound = Gdx.audio.newSound(Gdx.files.internal("assets/hitHurt.wav"))
        winSound = Gdx.audio.newSound(Gdx.files.internal("assets/trumpetTriumph.mp3"))
        looseSound = Gdx.audio.newSound(Gdx.files.internal("assets/playerDeath.wav"))
        powerupSound = Gdx.audio.newSound(Gdx.files.internal("assets/powerup.wav"))
        windMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/wind.mp3"))
        windMusic.isLooping = true
        windMusic.volume = .1f
        windMusic.play()
    }

    override fun update(dt: Float) {
        for (wall in BaseActor.getList(mainStage, Wall::class.java.canonicalName)) {
            hero.preventOverlap(wall)
        }

        if (ghost.actions.size == 0) {
            maze.resetRooms()
            ghost.findPath(maze.getRoom(ghost)!!, maze.getRoom(hero)!!)
        }

        for (coin in BaseActor.getList(mainStage, Coin::class.java.canonicalName)) {
            if (hero.overlaps(coin)) {
                coin.remove()
                coinSound.play()
            }
        }

        val coins = BaseActor.count(mainStage, Coin::class.java.canonicalName)
        coinsLabel.setText("Coins left: $coins")

        if (coins == 0) {
            ghost.remove()
            ghost.setPosition(-1000f, -1000f)
            ghost.clearActions()
            ghost.addAction(Actions.forever(Actions.delay(1f)))
            messageLabel.setText("You win!")
            messageLabel.color = Color.GREEN
            messageLabel.isVisible = true
            winSound.play()
        }

        if (hero.overlaps(ghost)) {
            hurtSound.play()
            hero.remove()
            hero.setPosition(-1000f, -1000f)
            ghost.addAction(Actions.forever(Actions.delay(1f)))
            messageLabel.setText("Game Over")
            messageLabel.color = Color.RED
            messageLabel.isVisible = true
            looseSound.play()
        }

        if (!messageLabel.isVisible) {
            val distance = Vector2(hero.x - ghost.x, hero.y - ghost.y).len()
            var volume = -(distance - 64) / (300 - 64) + 1
            volume = MathUtils.clamp(volume, .1f, 1f)
            windMusic.volume = volume
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.R) {
            powerupSound.play(.125f)
            dispose()
            BaseGame.setActiveScreen(LevelScreen())
        }
        return false
    }

    override fun dispose() {
        super.dispose()
        windMusic.dispose()
        coinSound.dispose()
    }
}
