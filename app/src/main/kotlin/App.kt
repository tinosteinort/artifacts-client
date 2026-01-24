import artifacts.adapter.artifactsmmo.ArtifactsGameCore
import artifacts.adapter.cli.CliAdapter
import artifacts.business.Game
import artifacts.business.util.GameException
import artifacts.business.util.Outcome
import java.net.http.HttpClient

class App

fun main() {

    val coreResult = ArtifactsGameCore.create(
        httpClient = HttpClient.newHttpClient(),
        artifactsApiUrl = "https://api.artifactsmmo.com",
        authToken = System.getenv("API_TOKEN")
    )

    when (coreResult) {
        is Outcome.Error -> throw GameException(coreResult.value)
        is Outcome.Success -> {}
    }

    val game = Game(coreResult.value)
    game.start()

    CliAdapter(game).run()
}
