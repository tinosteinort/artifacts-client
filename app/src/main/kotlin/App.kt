import artifacts.adapter.ArtifactsGame
import artifacts.business.Figure
import artifacts.business.FigureAutoController
import artifacts.business.Game
import artifacts.business.action.FightAction
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
    val henkController = FigureAutoController(henk, true)

    henk.setActions(
        listOf(
            //MoveAction { Places.CHICKEN },
            FightAction(),
        )
    )

    while (true) {
        henk.executeAction()
        henkController.control()
        Thread.sleep(1000)
    }
}