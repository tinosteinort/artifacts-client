package artifacts.logic

import artifacts.business.Behaviour
import artifacts.business.DefaultActions
import artifacts.business.Figure
import artifacts.business.GameCore
import artifacts.business.common.*
import artifacts.business.result.FightResult
import artifacts.business.util.Loggers

class Fighter(
    private val core: GameCore,
    private val figure: Figure,
    private val monster: Monster,
) : Behaviour {

    private var fightFailed: Boolean = false

    override fun control(): Cooldown {
        if (needsHeal()) {
            return heal()
        }

        findMonster(monster)
            ?.let { positionOfMonster ->
                if (positionOfMonster != figure.data().position) {
                    return DefaultActions.move(logger, figure, positionOfMonster)
                }
            }
            ?: return failed("monster $monster not found")

        return fight()
    }

    private fun fight(): Cooldown =
        when (val result = figure.fight()) {
            is FightResult.FightEnded -> {
                if (result.win) {
                    logger.info("${figure.name} won the fight against ${result.opponent}")
                } else {
                    failed("fight lost against ${result.opponent}")
                }
                result.cooldown
            }

            is FightResult.CharacterIsInCooldown -> {
                logger.info("${figure.name} is in cooldown")
                Cooldown.NO_COOLDOWN
            }

            is FightResult.InventoryFull -> {
                failed("inventory of ${figure.name} is full")
            }

            is FightResult.NoMonsterOnMap -> {
                failed("cannot fight, no monster on map")
            }

            is FightResult.OnlyBossMonsterCanBeFoughtByMultipleCharacters -> {
                failed("only boss monster can be fought by multiple characters")
            }
        }

    private fun needsHeal(): Boolean =
        with(figure.data().status) {
            hp < maxHp
        }

    private fun heal(): Cooldown =
        when (val method = HealingMethod.forFigure(figure.data())) {
            is HealingMethod.WithItem -> with(method) {
                DefaultActions.useItem(logger, figure, item, quantity)
            }

            is HealingMethod.WithoutItem -> DefaultActions.rest(logger, figure)
        }

    private fun findMonster(monster: Monster): Position? =
        core.maps()
            .filter { map -> map.content?.type == ContentType.MONSTER }
            .firstOrNull { map -> map.content!!.code == monster.value }
            ?.position

    private fun failed(message: String): Cooldown {
        fightFailed = true
        logger.error("${figure.name}: fight failed: $message")
        return Cooldown.NO_COOLDOWN
    }

    companion object {
        val logger = Loggers.getLogger(Fighter::class.java)
    }
}
