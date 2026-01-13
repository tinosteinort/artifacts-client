import artifacts.adapter.ArtifactsGameCore
import artifacts.business.Game
import artifacts.business.Places
import artifacts.business.action.FightAction
import artifacts.business.action.MoveAction
import artifacts.business.action.RestAction
import java.net.http.HttpClient

class App

fun main() {

    val game = Game(
        ArtifactsGameCore(
            httpClient = HttpClient.newHttpClient(),
            artifactsApiUrl = "https://api.artifactsmmo.com",
            authToken = System.getenv("API_TOKEN")
        )
    )
    game.registerFigure("Henk")
    game.start()

    println("type 'exit' to quit")
    do {
        print("> ")
        val line = readlnOrNull()

        when (line) {
            "exit" -> {
                game.stop()
            }

            "Henk fight chicken" -> {
                game.autoControl("Henk") { figure ->
                    figure.setActions(
                        listOf(
                            RestAction(),
                            MoveAction { Places.CHICKEN },
                            FightAction(),
                            FightAction(),
                        )
                    )
                }
            }

            "Henk rest" -> {
                game.control("Henk") { figure ->
                    figure.setActions(listOf(RestAction()))
                }
            }

            "Henk auto off" -> {
                game.autoControlOff("Henk")
            }

            else -> {
                println("unknown command")
            }
        }
    } while (game.running)
}
