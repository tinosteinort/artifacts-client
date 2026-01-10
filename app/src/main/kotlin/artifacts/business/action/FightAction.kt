package artifacts.business.action

import artifacts.business.Action
import artifacts.business.GameCore
import artifacts.business.common.Cooldown
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome

class FightAction : Action {

    override fun execute(core: GameCore, figureName: String): Cooldown {
        when (val result = core.fight(figureName)) {
            is Outcome.Error -> logger.error("${result.value::class.java}")
            is Outcome.Success -> when (result.value) {
                is FightResult.FightEnded -> {
                    if (result.value.win) {
                        logger.info("$figureName won the fight against ${result.value.opponent}")
                    } else {
                        logger.info("$figureName lost the fight against ${result.value.opponent}")
                    }
                    return result.value.cooldown
                }

                is FightResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                is FightResult.InventoryFull -> logger.info("inventory of $figureName is full")
                is FightResult.NoMonsterOnMap -> logger.info("$figureName cannot fight, no monster on map")
                is FightResult.OnlyBossMonsterCanBeFoughtByMultipleCharacters ->
                    logger.info("$figureName: only boss monster can be fought by multiple characters")
            }
        }

        return Cooldown.forSeconds(5)
    }

    companion object {
        val logger = Loggers.getLogger(FightAction::class.java)
    }
}
