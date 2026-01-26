import artifacts.adapter.artifactsmmo.ArtifactsGameCore
import artifacts.adapter.cli.CliAdapter
import artifacts.business.Game
import artifacts.business.util.GameException
import artifacts.business.util.Outcome
import java.net.http.HttpClient

class App

fun main() {

    val core = ArtifactsGameCore.create(
        httpClient = HttpClient.newHttpClient(),
        artifactsApiUrl = "https://api.artifactsmmo.com",
        authToken = System.getenv("API_TOKEN")
    )

    when (core) {
        is Outcome.Error -> throw GameException(core.value)
        is Outcome.Success -> {
            val game = Game(core.value)
            game.start()

            CliAdapter(game).run()
        }
    }
}
