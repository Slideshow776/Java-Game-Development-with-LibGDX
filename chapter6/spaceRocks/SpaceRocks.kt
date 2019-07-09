package chapter6.spaceRocks

import com.badlogic.gdx.backends.lwjgl.LwjglApplication

object SpaceRocks {
    @JvmStatic
    fun main(args: Array<String>) {
        val myGame = SpaceGame()
        LwjglApplication(myGame, "Space Rocks", 800, 600)
    }
}