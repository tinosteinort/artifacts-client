package artifacts.business

import artifacts.business.common.Cooldown

class Figure(
    private val core: GameCore,
    private val figureName: String,
) {

    private var currentAction: Action? = null
    private val actions: MutableList<Action> = mutableListOf()
    private var cooldown: Cooldown? = null

    fun setAction(action: Action) {
        this.actions.add(action)
    }

    fun executeAction() {

        if (currentAction == null && actions.isNotEmpty()) {
            currentAction = actions.removeFirst()
            cooldown = currentAction?.execute(core, figureName)
        } else {
            if (!inCooldown()) {
                currentAction = null
                cooldown = null
            }
        }
    }

    private fun inCooldown(): Boolean =
        cooldown?.inCooldown() ?: false

    fun isBusy(): Boolean =
        currentAction != null
                || actions.isNotEmpty()
                || inCooldown()
}