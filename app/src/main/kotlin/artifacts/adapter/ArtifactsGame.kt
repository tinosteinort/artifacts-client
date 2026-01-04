package artifacts.adapter

import artifacts.adapter.dto.FightResponseDto
import artifacts.adapter.dto.MoveResponseDto
import artifacts.business.Game
import artifacts.business.action.FightResult
import artifacts.business.action.MoveResult
import artifacts.business.common.Cooldown
import artifacts.business.common.GameError
import artifacts.business.util.Loggers
import artifacts.business.common.Position
import artifacts.business.util.Outcome
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers

class ArtifactsGame(
    private val httpClient: HttpClient,
    private val artifactsApiUrl: String,
    private val authToken: String
) : Game {

    private val json = Json {
        ignoreUnknownKeys = true
    }

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
            200 -> {
                val moveData = json.decodeFromString<MoveResponseDto>(response.body())
                Outcome.success(
                    MoveResult.Success(
                        cooldown = Cooldown.forSeconds(
                            moveData.data.cooldown.remaining_seconds
                        )
                    )
                )
            }

            404 -> Outcome.error(GameError.MapNotFound())
            422 -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            486 -> Outcome.success(MoveResult.CharacterIsBusy())
            490 -> Outcome.success(MoveResult.AlreadyThere())
            496 -> Outcome.success(MoveResult.ConditionsNotMet())
            498 -> Outcome.error(GameError.CharacterNotFound())
            499 -> Outcome.success(MoveResult.CharacterIsInCooldown())
            595 -> Outcome.error(GameError.NoPathAvailable())
            596 -> Outcome.success(MoveResult.MapIsBlocked())
            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    override fun fight(character: String): Outcome<FightResult, GameError> {
        val request = HttpRequest
            .newBuilder(URI("$artifactsApiUrl/my/${character}/action/fight"))
            .configureHeaders()
            .POST(BodyPublishers.noBody())
            .build()

        val response = httpClient.send(request, BodyHandlers.ofString())

        return when (response.statusCode()) {
            200 -> {
                val fightData = json.decodeFromString<FightResponseDto>(response.body())

                Outcome.success(
                    FightResult.FightEnded(
                        win = fightData.data.fight.result == "win",
                        opponent = fightData.data.fight.opponent,
                        cooldown = Cooldown.forSeconds(
                            fightData.data.cooldown.remaining_seconds
                        ),
                    )
                )
            }

            422 -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
            486 -> Outcome.success(FightResult.OnlyBossMonsterCanBeFoughtByMultipleCharacters())
            497 -> Outcome.success(FightResult.InventoryFull())
            498 -> Outcome.error(GameError.CharacterNotFound())
            499 -> Outcome.success(FightResult.CharacterIsInCooldown())
            598 -> Outcome.success(FightResult.NoMonsterOnMap())
            else -> Outcome.error(GameError.Generic("HTTP${response.statusCode()} - ${response.body()}"))
        }
    }

    private fun HttpRequest.Builder.configureHeaders(): HttpRequest.Builder {
        this.header("Authorization", "Bearer $authToken")
        this.header("Accept", "application/json")
        this.header("Content-Type", "application/json")
        return this
    }

    companion object {

        private val logger = Loggers.getLogger(ArtifactsGame::class.java)
    }
}
