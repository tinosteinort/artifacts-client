import artifacts.adapter.artifactsmmo.ArtifactsGameCore
import artifacts.adapter.cli.CliAdapter
import artifacts.business.Game
import artifacts.business.common.Name
import java.net.http.HttpClient

class App

fun main() {

    val game = Game(
        ArtifactsGameCore(
            httpClient = HttpClient.newHttpClient(),
            artifactsApiUrl = "https://api.artifactsmmo.com",
            authToken = System.getenv("API_TOKEN")
        )
    )
    game.start()

    CliAdapter(game).run()
}
