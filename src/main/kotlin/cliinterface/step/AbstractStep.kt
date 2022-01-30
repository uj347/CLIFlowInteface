package cliinterface.step

import cliinterface.properties.AbstractPropety
import cliinterface.properties.SimpleProperty
import cliinterface.properties.isAuthoredByStepOrDescendant
import cliinterface.stack.IStackController
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.IllegalArgumentException

interface AbstractStep : Flow<Unit> {
    val name: String

    var parentStep: AbstractStep?

    val adress: LinkedList<String>

    var stateToken: MutableSet<AbstractPropety<*>>?

    val layoutEtap: suspend Flow<Unit>.() -> Flow<Unit>

    val inputEtap: suspend Flow<Unit>.() -> Flow<String>

    val processingEtap: suspend Flow<String>.() -> Flow<Unit>

    val onCompletionBlock: suspend FlowCollector<Unit>.(Throwable?) -> Unit

    var runStackController: IStackController?

    override suspend fun collect(collector: FlowCollector<Unit>) {
        cleanUpProperties().layoutEtap().inputEtap().processingEtap().onCompletion(onCompletionBlock).collect(collector)
    }


    fun associateWithStackController(stackController: IStackController) {
        this.runStackController = stackController
        stateToken = runStackController?.stateToken
    }

    fun cleanUpProperties(): Flow<Unit> = flow {
        stateToken?.removeIf { it.isAuthoredByStepOrDescendant(name) }
        emit(Unit)
    }


    fun repeatSelf() {
        runStackController?.push(this)
    }

    fun addAncestorStep(ancestorStep: AbstractStep) {
        if (ancestorStep.adress.contains(this.name)) {
            throw IllegalArgumentException(
                "Hierarchy broken, you passed child step as ancestor step"
            )
        }
        runStackController?.push(ancestorStep)
    }

    fun addNewChildStep(newStep: AbstractStep) {
        newStep.mutateToChildOf(this)
        runStackController?.push(newStep)
    }

    fun <T> addAssociatedProperty(name: String, value: T) {
        stateToken?.add(SimpleProperty<T>(name, this, value))
    }


    suspend fun <T, R> Flow<T>.flowChainProcessingInsertion(
        block: suspend (received: T, selfReference: AbstractStep, runStackController: IStackController?) -> R
    ): Flow<R> {
        return flow<R> {
            this@flowChainProcessingInsertion.collect {
                emit(block(it, this@AbstractStep, runStackController))
            }
        }
    }
}


suspend fun <T, R> Flow<T>.flowChainInsertion(
    block: suspend (T) -> R
): Flow<R> {
    return flow<R> {
        this@flowChainInsertion.collect {
            emit(block(it))
        }
    }
}

fun oneLineCliInputBlock(): String {
    return Scanner(System.`in`).next()
}

val emptyCompletionBlock: FlowCollector<String?>.(Throwable?) -> Unit = { _ -> }


fun AbstractStep.mutateToChildOf(newParentStep: AbstractStep): AbstractStep {
    parentStep = newParentStep
    return this
}

fun AbstractStep.getParentName(): String {
    if (adress.size == 1) {
        return ""
    } else {
        return adress.get(adress.size - 2)
    }
}

fun AbstractStep.isChildOf(step: AbstractStep): Boolean {
    return getParentName() == step.name
}

fun AbstractStep.isSame(targetStep: AbstractStep): Boolean {
    return name == targetStep.name
}

fun AbstractStep.isAncestorOf(targetStep: AbstractStep): Boolean {
    return !isSame(targetStep) && name in targetStep.adress
}

fun AbstractStep.isSameOrAncestorOf(targetStep: AbstractStep): Boolean {
    return name in targetStep.adress
}

fun AbstractStep.isDescendantOf(targetStep: AbstractStep): Boolean {
    return !isSame(targetStep) && targetStep.name in adress
}

fun AbstractStep.isSameOrDescendantOf(targetStep: AbstractStep): Boolean {
    return targetStep.name in adress
}

fun AbstractStep.isParentOf(step: AbstractStep): Boolean {
    return step.getParentName() == name
}

