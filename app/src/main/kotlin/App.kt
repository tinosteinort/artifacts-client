import artifacts.adapter.ArtifactsGameCore
import artifacts.business.Figure
import artifacts.business.FigureAutoController
import artifacts.business.GameCore
import artifacts.business.Items
import artifacts.business.action.CraftAction
import java.net.http.HttpClient

class App

fun main() {

    val httpClient = HttpClient.newHttpClient()
    val core: GameCore = ArtifactsGameCore(
        httpClient = httpClient,
        artifactsApiUrl = "https://api.artifactsmmo.com",
        authToken = System.getenv("API_TOKEN")
    )

    val henk = Figure(core, "Henk")
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