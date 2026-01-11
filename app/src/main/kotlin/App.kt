import artifacts.adapter.ArtifactsGameCore
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
    game.registerFigure("Henk")
    game.run()

    game.autoControl("Henk") { figure ->
        figure.setActions(
            //listOf(
            //    RestAction(),
            //    MoveAction {
            //        Places.CHICKEN
            //    },
            //    FightAction(),
            //    FightAction(),
            //)
            listOf(
                CraftAction(Items.COOKED_CHICKEN, 1)
            )
        )
    }
}