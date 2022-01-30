package cliinterface.stack

import cliinterface.properties.AbstractPropety
import cliinterface.step.AbstractStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface IStackController {
    val firstStep: AbstractStep
    val terminalStep: AbstractStep?
    val stateToken: MutableSet<AbstractPropety<*>>
    val controllerScope: CoroutineScope
    val isEmpty: Boolean

    fun push(newStep: AbstractStep)
    fun runChain(): Job
    fun removeAllStepsForSameChain(withStep: AbstractStep): Boolean
    fun peek(): AbstractStep
    fun containsStepForName(stepName: String): Boolean
    fun size(): Int
}