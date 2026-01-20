package artifacts.logic

import artifacts.business.Behaviour
import artifacts.business.Figure
import artifacts.business.GameCore
import artifacts.business.common.Cooldown
import artifacts.business.result.RestResult
import artifacts.business.util.Loggers
import artifacts.business.util.Outcome

class Fighter(
    private val core: GameCore,
    private val figure: Figure,
) : Behaviour {

    override fun init() = when (core.init(figure.name)) {
        is Outcome.Error -> throw RuntimeException("could not init ${figure.name}")
        is Outcome.Success -> {
            logger.info("init done for ${figure.name}")
        }
    }

    override fun control(): Cooldown {
        if (shouldHeal()) {
            heal()
        }

        return Cooldown.NO_COOLDOWN
    }

    private fun shouldHeal(): Boolean =
        with(figure.status()) {
            hp < maxHp
        }

    private fun heal() =
        when (val result = figure.rest()) {
            is Outcome.Error -> logger.error(result.value)
            is Outcome.Success -> when (result.value) {
                is RestResult.CharacterIsBusy -> logger.info("${figure.name} is busy")
                is RestResult.CharacterIsInCooldown -> logger.info("${figure.name} is in cooldown")
                is RestResult.Success -> logger.info("${figure.name} did a rest")
            }
        }

    companion object {
        val logger = Loggers.getLogger(Fighter::class.java)
    }
}
