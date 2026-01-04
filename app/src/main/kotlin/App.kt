import artifacts.adapter.ArtifactsGame
import artifacts.business.Game
import artifacts.business.GameController
import java.net.http.HttpClient

class App

fun main() {

    val httpClient = HttpClient.newHttpClient()
    val game: Game = ArtifactsGame(
        httpClient = httpClient,
        artifactsApiUrl = "https://api.artifactsmmo.com",
        authToken = System.getenv("API_TOKEN")
    )

    GameController(game, "Henk").run()
}