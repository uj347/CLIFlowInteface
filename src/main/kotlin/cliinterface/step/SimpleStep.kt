package cliinterface.step

import cliinterface.properties.AbstractPropety
import cliinterface.stack.DaggerStackController
import cliinterface.stack.IStackController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import java.util.*

open class SimpleStep(
    override val name: String,
    override var parentStep: AbstractStep?,
    layoutBlock: suspend (MutableSet<AbstractPropety<*>>?) -> Unit,
    inputBlock: suspend (Unit) -> String = { oneLineCliInputBlock() },
    processingBlock: suspend (String, AbstractStep, IStackController?) -> Unit,
    override val onCompletionBlock: suspend FlowCollector<Unit>.(Throwable?) -> Unit = { emptyCompletionBlock }
) : AbstractStep {

    override var runStackController: IStackController? = null
    override var stateToken: MutableSet<AbstractPropety<*>>? = null
    override val layoutEtap: suspend Flow<Unit>.() -> Flow<Unit> = { flowChainLayoutInsertion(layoutBlock) }
    override val inputEtap: suspend Flow<Unit>.() -> Flow<String> = { flowChainInsertion(inputBlock) }
    override val processingEtap: suspend Flow<String>.() -> Flow<Unit> =
        { flowChainProcessingInsertion(processingBlock) }


    override val adress: LinkedList<String>
        get() {
            return parentStep?.let {
                LinkedList(parentStep?.adress ?: LinkedList<String>()).apply { add(name) }
            }
                ?: LinkedList<String>().apply { add(name) }
        }

}