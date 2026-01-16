import artifacts.adapter.artifactsmmo.ArtifactsGameCore
import artifacts.adapter.cli.CliAdapter
import artifacts.business.Game
import artifacts.business.Items
import artifacts.business.Places
import artifacts.business.action.*
import artifacts.business.controller.Fighter
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome
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

    CliAdapter(game).run()
}
