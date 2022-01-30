package cliinterface.step.stepoptions

import cliinterface.stack.IStackController
import cliinterface.step.AbstractStep
import kotlin.collections.LinkedHashSet

class StepOptionBundle : MutableSet<StepOption> by LinkedHashSet<StepOption>() {
    fun getOptionByName(idName: String): StepOption? {
        return this.firstOrNull { it.idName == idName }
    }

    fun getCommandBlockByName(idName: String): (suspend (String, AbstractStep, IStackController?) -> Unit)? {
        return this.firstOrNull { it.idName == idName }?.reaction
    }

    fun getOptionDisplayableNameById(idName: String): String? {
        return this.firstOrNull { it.idName == idName }?.visibleText
    }

}


fun StepOptionBundle.option(block: StepOption.StepOtionBuilder.() -> Unit) {
    StepOption.StepOtionBuilder().apply {
        block()
        add(build())
    }
}

fun stepOptionBundle(block: StepOptionBundle.() -> Unit): StepOptionBundle {
    val bundle = StepOptionBundle()
    bundle.block()
    return bundle
}