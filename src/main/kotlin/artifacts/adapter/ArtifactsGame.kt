package artifacts.adapter

import artifacts.business.Game
import artifacts.business.common.GameError
import artifacts.business.MoveResult
import artifacts.business.common.Loggers
import artifacts.business.common.Position
import artifacts.business.util.Outcome
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

class ArtifactsGame(
    private val httpClient: HttpClient,
    private val artifactsApiUrl: String,
    private val authToken: String
) : Game {

    override fun move(character: String, position: Position): Outcome<MoveResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/${character}/action/move"))
            .configureHeaders()
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """
                    {
                      "x": ${position.x},
                      "y": ${position.y}
                    }    
                    """.trimIndent()
                )
            )
            .build()

        val response = httpClient.send(request, BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> return Outcome.success(MoveResult.Success())
            404 -> return Outcome.error(GameError.MapNotFound())
            422 -> return Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            486 -> return Outcome.success(MoveResult.CharacterIsBusy())
            490 -> return Outcome.success(MoveResult.AlreadyThere())
            496 -> return Outcome.success(MoveResult.ConditionsNotMet())
            498 -> return Outcome.error(GameError.CharacterNotFound())
            499 -> return Outcome.success(MoveResult.CharacterIsInCooldown())
            595 -> return Outcome.error(GameError.NoPathAvailable())
            596 -> return Outcome.success(MoveResult.MapIsBlocked())
            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    private fun HttpRequest.Builder.configureHeaders(): HttpRequest.Builder {
        this.header("Authorization", "Bearer $authToken")
        this.header("Accept", "application/json")
        this.header("Content-Type", "application/json")
        return this
    }
}
