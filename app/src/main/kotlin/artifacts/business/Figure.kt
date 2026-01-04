package artifacts.business

import artifacts.business.common.Cooldown

class Figure(
    private val game: Game,
    private val figureName: String,
) {

    private var currentAction: Action? = null
    private val actions: MutableList<Action> = mutableListOf()
    private var cooldown: Cooldown? = null

    fun setActions(actions: List<Action>) {
        this.actions.clear()
        this.actions.addAll(actions)
    }

    fun executeAction() {

        if (currentAction == null && actions.isNotEmpty()) {
            currentAction = actions.removeFirst()
            cooldown = currentAction?.execute(game, figureName)
        } else {
            if (!inCooldown()) {
                currentAction = null
                cooldown = null
            }
        }
    }

    private fun inCooldown(): Boolean {
        return false // TODO
    }

    fun isBusy(): Boolean =
        currentAction != null
                || actions.isNotEmpty()
                || inCooldown()
}