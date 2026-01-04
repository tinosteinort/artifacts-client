package artifacts.business.action

import artifacts.business.Action
import artifacts.business.Game
import artifacts.business.common.Cooldown
import artifacts.business.common.Loggers
import artifacts.business.util.Outcome

class FightAction : Action {

    override fun execute(game: Game, figureName: String): Cooldown {
        when (val result = game.fight(figureName)) {
            is Outcome.Error -> logger.error("${result.value::class.java}")
            is Outcome.Success -> when (result.value) {
                is FightResult.FightEnded -> {
                    logger.info("fight ended")
                }

                is FightResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                is FightResult.InventoryFull -> logger.info("inventory of $figureName is full")
                is FightResult.NoMonsterOnMap -> logger.info("$figureName cannot fight, no monster on map")
                is FightResult.OnlyBossMonsterCanBeFoughtByMultipleCharacters ->
                    logger.info("$figureName: only boss monster can be fought by multiple characters")
            }
        }

        return Cooldown()
    }

    companion object {
        val logger = Loggers.getLogger(FightAction::class.java)
    }
}
