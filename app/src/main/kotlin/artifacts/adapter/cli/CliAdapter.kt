package artifacts.adapter.cli

import artifacts.business.DefaultActions
import artifacts.business.Game
import artifacts.business.common.*
import artifacts.business.util.Loggers
import artifacts.logic.Fighter
import artifacts.logic.Follower

class CliAdapter(private val game: Game) {

    private fun readCommand(): String {
        print("> ")
        return readln()
    }

    private fun readName(): Name {
        print("name > ")
        return Name(readln())
    }

    private fun readMonster(): Monster {
        print("name > ")
        return Monster(readln())
    }

    private fun readPosition(): Position {
        print("position > ")
        return readln()
            .split(",")
            .let {
                Position(
                    it[0].toInt(),
                    it[1].toInt(),
                )
            }
    }

    private fun readItem(): Item.Name {
        print("item > ")
        return Item.Name(readln())
    }

    private fun readSlot(): Slot {
        print("slot > ")
        return Slot.valueOf(readln())
    }

    private fun readQuantity(): Int {
        print("quantity > ")
        return readln().toInt()
    }

    fun run() {
        println("type name of figure to control")
        var name = readName()
        println("type 'exit' to quit")
        do {
            val line = readCommand().trim()

            when (line) {
                "exit" -> {
                    game.stop()
                    break
                }

                "figure" -> name = readName()

                "move" -> {
                    val position = readPosition()
                    game.control(name) { figure ->
                        DefaultActions.move(logger, figure, position)
                    }
                }

                "fight" -> {
                    game.control(name) { figure ->
                        DefaultActions.fight(logger, figure)
                    }
                }

                "rest" -> {
                    game.control(name) { figure ->
                        DefaultActions.rest(logger, figure)
                    }
                }

                "gather" -> {
                    game.control(name) { figure ->
                        DefaultActions.gather(logger, figure)
                    }
                }

                "craft" -> {
                    val item = readItem()
                    game.control(name) { figure ->
                        DefaultActions.craft(logger, figure, item, 1)
                    }
                }

                "equip" -> {
                    val item = readItem()
                    val slot = readSlot()
                    val quantity = readQuantity()
                    game.control(name) { figure ->
                        DefaultActions.equip(logger, figure, item, slot, quantity)
                    }
                }

                "unequip" -> {
                    val slot = readSlot()
                    val quantity = readQuantity()
                    game.control(name) { figure ->
                        DefaultActions.unequip(logger, figure, slot, quantity)
                    }
                }

                "use item" -> {
                    val item = readItem()
                    val quantity = readQuantity()
                    game.control(name) { figure ->
                        DefaultActions.useItem(logger, figure, item, quantity)
                    }
                }

                "give item" -> {
                    val target = readName()
                    val item = readItem()
                    val quantity = readQuantity()
                    game.control(name) { figure ->
                        DefaultActions.giveItem(logger, figure, target, item, quantity)
                    }
                }

                "follow" -> {
                    val target = readName()
                    game.autoControl(name) { core, figure ->
                        Follower(core, figure, target)
                    }
                }

                "fighter" -> {
                    val position = readPosition()
                    game.autoControl(name) { _, figure ->
                        Fighter(figure, position)
                    }
                }

                "auto off" -> game.autoControlOff(name)
                else -> println("unknown command")
            }
        } while (game.running)
    }

    companion object {
        val logger = Loggers.getLogger(CliAdapter::class.java)
    }
}
