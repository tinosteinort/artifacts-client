package artifacts.logic

import artifacts.business.Controller
import artifacts.business.Figure
import artifacts.business.Places
import artifacts.business.action.FightResult
import artifacts.business.action.MoveResult
import artifacts.business.common.Cooldown
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome

class Fighter : Controller {

    override fun control(figure: Figure): Cooldown {

        return move(figure)
        //return fight(figure)
    }

    private fun fight(figure: Figure): Cooldown {
        return when (val result = figure.fight()) {
            is Outcome.Error -> {
                logger.error(result.value)
                Cooldown.NO_COOLDOWN
            }

            is Outcome.Success -> when (result.value) {
                is FightResult.FightEnded -> {
                    if (result.value.win) {
                        logger.info("${figure.name} won the fight against ${result.value.opponent}")
                    } else {
                        logger.info("${figure.name} lost the fight against ${result.value.opponent}")
                    }
                    result.value.cooldown
                }

                is FightResult.CharacterIsInCooldown -> {
                    logger.info("${figure.name} is in cooldown")
                    Cooldown.NO_COOLDOWN
                }

                is FightResult.InventoryFull -> {
                    logger.info("inventory of ${figure.name} is full")
                    Cooldown.NO_COOLDOWN
                }

                is FightResult.NoMonsterOnMap -> {
                    logger.info("${figure.name} cannot fight, no monster on map")
                    Cooldown.NO_COOLDOWN
                }

                is FightResult.OnlyBossMonsterCanBeFoughtByMultipleCharacters -> {
                    logger.info("${figure.name}: only boss monster can be fought by multiple characters")
                    Cooldown.NO_COOLDOWN
                }
            }
        }
    }

    private fun move(figure: Figure): Cooldown =
        when (val result = figure.move(Places.COWS)) {
            is Outcome.Error -> {
                logger.error(result.value)
                Cooldown.NO_COOLDOWN
            }

            is Outcome.Success -> when (result.value) {
                is MoveResult.AlreadyThere -> {
                    logger.info("${figure.name} is already there")
                    Cooldown.NO_COOLDOWN
                }

                is MoveResult.CharacterIsBusy -> {
                    logger.info("${figure.name} is busy")
                    Cooldown.NO_COOLDOWN
                }

                is MoveResult.CharacterIsInCooldown -> {
                    logger.info("${figure.name} is in cooldown")
                    Cooldown.NO_COOLDOWN
                }

                is MoveResult.ConditionsNotMet -> {
                    logger.info("${figure.name} does not match conditions")
                    Cooldown.NO_COOLDOWN
                }

                is MoveResult.MapIsBlocked -> {
                    logger.info("map is blocked")
                    Cooldown.NO_COOLDOWN
                }

                is MoveResult.Success -> {
                    logger.info("${figure.name} move done")
                    result.value.cooldown
                }
            }
        }

    companion object {
        val logger = Loggers.getLogger(Fighter::class.java)
    }
}
