import artifacts.adapter.ArtifactsGameCore
import artifacts.business.Game
import artifacts.business.action.RestAction
import artifacts.business.controller.Fighter
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

            "Henk rest" -> {
                game.control("Henk") { figure ->
                    figure.setAction(RestAction())
                }
            }

            "Henk fight" -> {
                game.autoControl("Henk", Fighter())
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
