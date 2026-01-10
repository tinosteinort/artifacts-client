package artifacts.business

class Game(core: GameCore) {

    var running: Boolean = true

    fun run(code: () -> Unit) {

        while (running) {
            code()
            Thread.sleep(1000)
        }
    }
}