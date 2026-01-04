import artifacts.adapter.ArtifactsGame
import artifacts.business.Figure
import artifacts.business.Game
import artifacts.business.Places
import artifacts.business.action.MoveAction
import java.net.http.HttpClient

class App

fun main() {

    val httpClient = HttpClient.newHttpClient()
    val game: Game = ArtifactsGame(
        httpClient = httpClient,
        artifactsApiUrl = "https://api.artifactsmmo.com",
        authToken = System.getenv("API_TOKEN")
    )

    val henk = Figure(game, "Henk")

    henk.setActions(
        listOf(
            MoveAction { Places.CHICKEN },
            //FightAction(),
        )
    )
    henk.executeAction()

    //while (true) {
    //    Thread.sleep(1000)
    //}
}