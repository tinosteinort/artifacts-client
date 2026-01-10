import artifacts.adapter.ArtifactsGame
import artifacts.business.Figure
import artifacts.business.FigureAutoController
import artifacts.business.Game
import artifacts.business.Items
import artifacts.business.Places
import artifacts.business.action.CraftAction
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
    val henkController = FigureAutoController(henk, true)

    henk.setActions(
        listOf(
            //MoveAction { Places.WORKSHOP_COOKING },
            CraftAction(Items.COOKED_CHICKEN, 14)
        )
    )

    while (true) {
        henk.executeAction()
        henkController.control()
        Thread.sleep(1000)
    }
}