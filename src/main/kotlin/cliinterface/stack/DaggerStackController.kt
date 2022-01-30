package cliinterface.stack

import cliinterface.ControllerContext
import cliinterface.FIRST_STEP_LITERAL
import cliinterface.StateToken
import cliinterface.TERMINAL_STEP_LITERAL
import cliinterface.properties.AbstractPropety
import cliinterface.step.AbstractStep
import cliinterface.step.isSameOrDescendantOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.coroutines.CoroutineContext



class DaggerStackController @AssistedInject constructor(@Assisted(FIRST_STEP_LITERAL) override val firstStep:AbstractStep,
                                                        @Assisted (TERMINAL_STEP_LITERAL) override val terminalStep:AbstractStep?,
                                                        @StateToken override val stateToken: MutableSet<AbstractPropety<*>>,
                                                        @ControllerContext controllerContext: CoroutineContext) :
    IStackController {


    override val controllerScope:CoroutineScope= CoroutineScope(controllerContext+ SupervisorJob())
    private val _stack = ConcurrentLinkedDeque<AbstractStep>()

    init {
        _stack.push(firstStep)
    }


    override fun push(newStep:AbstractStep){
    removeAllStepsForSameChain(newStep)
    _stack.push(newStep)
    }

    override fun runChain():Job{
        return controllerScope.launch {
            while(_stack.isNotEmpty()){

                    _stack.pop().apply {
                        println("Current contents of stack is:  $_stack")
                        associateWithStackController(this@DaggerStackController)
                        collect()
                    }
                }

            terminalStep?.collect()
        }
    }


    override fun removeAllStepsForSameChain(withStep: AbstractStep)=_stack.removeIf {it.isSameOrDescendantOf(withStep)}


    override fun peek():AbstractStep=_stack.peek()

    override fun containsStepForName(stepName:String):Boolean {
        return _stack.filter { it.name == stepName }.count() > 0
    }

    override fun size():Int {
        return _stack.size
    }

    override val isEmpty
        get ()=_stack.isEmpty()

}