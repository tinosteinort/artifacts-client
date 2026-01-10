package artifacts.business.action

import artifacts.business.Action
import artifacts.business.GameCore
import artifacts.business.common.Cooldown
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome

class GatherAction : Action {

    override fun execute(core: GameCore, figureName: String): Cooldown {

        when (val result = core.gather(figureName)) {
            is Outcome.Error -> logger.error("${result.value::class.java}")
            is Outcome.Success -> when (result.value) {
                is GatherResult.CharacterIsBusy -> logger.info("$figureName is busy")
                is GatherResult.CharacterIsInCooldown -> logger.info("$figureName is in cooldown")
                is GatherResult.InventoryFull -> logger.info("inventory of $figureName is full")
                is GatherResult.NoResourceOnMap -> logger.info("$figureName: no resource on map")
                is GatherResult.SkillLevelTooLow -> logger.info("gathering skill level of $figureName too low")
                is GatherResult.Success -> {
                    logger.info("$figureName gathered")
                    return result.value.cooldown
                }
            }
        }

        return Cooldown.forSeconds(5)
    }

    companion object {
        val logger = Loggers.getLogger(GatherAction::class.java)
    }
}