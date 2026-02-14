import artifacts.adapter.artifactsmmo.ArtifactsGameCore
import artifacts.adapter.cli.CliAdapter
import artifacts.business.DefaultActions
import artifacts.business.Game
import artifacts.business.common.Name
import artifacts.business.common.Position
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome
import java.net.http.HttpClient

class App

val logger = Loggers.getLogger(App::class.java)

fun main() {

    val core = ArtifactsGameCore.create(
        httpClient = HttpClient.newHttpClient(),
        artifactsApiUrl = "https://api.artifactsmmo.com",
        authToken = System.getenv("API_TOKEN")
    )

    when (core) {
        is Outcome.Error -> logger.error("could not create ArtifactsGameCore", core.value)
        is Outcome.Success -> {
            val game = Game(core.value)
            game.start()

//            CliAdapter(game).run()
            game.control(Name("Henk")) { figure ->
                DefaultActions.move(logger, figure, Position(0, 0))
            }
        }
    }
}
