import artifacts.adapter.ArtifactsGameCore
import artifacts.business.Figure
import artifacts.business.FigureAutoController
import artifacts.business.Game
import artifacts.business.Items
import artifacts.business.action.CraftAction
import java.net.http.HttpClient

class App

fun main() {

    val httpClient = HttpClient.newHttpClient()
    val core = ArtifactsGameCore(
        httpClient = httpClient,
        artifactsApiUrl = "https://api.artifactsmmo.com",
        authToken = System.getenv("API_TOKEN")
    )

    val game = Game(core)

    val henk = Figure(core, "Henk")

    henk.setActions(
        listOf(
            //MoveAction { Places.WORKSHOP_COOKING },
            CraftAction(Items.COOKED_CHICKEN, 14)
        )
    )

    val henkController = FigureAutoController(henk, true)
    game.run {
        henk.executeAction()
        henkController.control()
    }
}