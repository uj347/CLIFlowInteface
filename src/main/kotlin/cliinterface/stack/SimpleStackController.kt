package cliinterface.stack

import cliinterface.properties.AbstractPropety
import cliinterface.step.AbstractStep
import cliinterface.step.isSameOrDescendantOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.coroutines.CoroutineContext

class SimpleStackController constructor(
    override val firstStep: AbstractStep,
    override val terminalStep: AbstractStep?,
    override val stateToken: MutableSet<AbstractPropety<*>>,
    controllerContext: CoroutineContext
) :
    IStackController {
    override val controllerScope: CoroutineScope = CoroutineScope(controllerContext + SupervisorJob())
    private val _stack = ConcurrentLinkedDeque<AbstractStep>()

    init {
        _stack.push(firstStep)
    }


    override fun push(newStep: AbstractStep) {
        removeAllStepsForSameChain(newStep)
        _stack.push(newStep)
    }

    override fun runChain(): Job {
        return controllerScope.launch {
            while (_stack.isNotEmpty()) {

                _stack.pop().apply {
                    println("Current contents of stack is:  $_stack")
                    associateWithStackController(this@SimpleStackController)
                    collect()
                }
            }

            terminalStep?.collect()
        }
    }


    override fun removeAllStepsForSameChain(withStep: AbstractStep) =
        _stack.removeIf { it.isSameOrDescendantOf(withStep) }


    override fun peek(): AbstractStep = _stack.peek()

    override fun containsStepForName(stepName: String): Boolean {
        return _stack.filter { it.name == stepName }.count() > 0
    }

    override fun size(): Int {
        return _stack.size
    }

    override val isEmpty
        get() = _stack.isEmpty()

}
