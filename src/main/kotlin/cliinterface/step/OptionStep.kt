package cliinterface.step


import cliinterface.properties.AbstractProperty
import cliinterface.stack.IStackController
import cliinterface.step.stepoptions.StepOptionBundle
import kotlinx.coroutines.flow.FlowCollector
import java.util.*
import java.util.regex.Pattern

class OptionStep private constructor(
    name: String,
    parentStep: AbstractStep?,
    multiOptional: Boolean,
    associationMap: Map<Int, Pair<String, suspend (String, AbstractStep, IStackController?) -> Unit>>,
    layoutBlock: suspend (received: Unit, selfReference: AbstractStep, runStackController: IStackController?) -> Unit,
    inputBlock: suspend (received: Unit, selfReference: AbstractStep, runStackController: IStackController?) -> String,
    processingBlock: suspend (String, AbstractStep, IStackController?) -> Unit,
    onCompletionBlock: suspend FlowCollector<Unit>.(Throwable?) -> Unit = { emptyCompletionBlock }
) : SimpleStep(
    name, parentStep, layoutBlock, inputBlock, processingBlock, onCompletionBlock
) {


    companion object {
        fun create(
            name: String,
            parentStep: AbstractStep?,
            multiOptional: Boolean,
            message: String,
            optionBundle: StepOptionBundle,
            firstAction:(suspend (String, AbstractStep, IStackController?) -> Unit)? = null,
            lastAction: (suspend (String, AbstractStep, IStackController?) -> Unit)? = null
        ): OptionStep {
            val associationMap = formOptionAssociationMap(optionBundle)
            val layoutBlock = generateLayoutBlock(associationMap, message)
            val processingBlock = generateProcessingBlock(associationMap, multiOptional,firstAction,lastAction)
            return OptionStep(
                name,
                parentStep,
                multiOptional,
                associationMap,
                layoutBlock,
                ::oneLineCliInputBlock,
                processingBlock
            )
        }

        const val GO_BACK_OPTION_LITERALL = "Back"

        private fun generateLayoutBlock(
            associationMap: Map<Int, Pair<String, suspend (String, AbstractStep, IStackController?) -> Unit>>,
            message: String
        ):
                suspend (Unit, AbstractStep, IStackController?) -> Unit {
            return { unit: Unit, abstractStep: AbstractStep, iStackController: IStackController? ->
                println(message)
                for (entry in associationMap) {
                    println("${entry.key}. ${entry.value.first}")
                }
            }
        }

        private fun generateProcessingBlock(
            associationMap: Map<Int, Pair<String,
                    suspend (String, AbstractStep, IStackController?) -> Unit>>, multiOptional: Boolean,
            firstAction: (suspend (String, AbstractStep, IStackController?) -> Unit)?,
            lastAction: (suspend (String, AbstractStep, IStackController?) -> Unit)?
        )
                : suspend (String, AbstractStep, IStackController?) -> Unit {
            return { strValue, selfRef, stackController ->
                if (checkStringInputValidity(strValue, multiOptional, associationMap)) {
                    val pattern = Pattern.compile("\\D+")
                    val intInput = strValue.trim().split(pattern).map { it.toInt() }
                    when {
                        intInput.contains(0) -> associationMap.get(0)?.second?.invoke(
                            strValue,
                            selfRef,
                            stackController
                        )
                        else -> {
                            firstAction?.invoke(strValue,selfRef,stackController)
                            for (integer in intInput) {
                                associationMap.get(integer)?.second?.invoke(strValue, selfRef, stackController)
                            }
                            lastAction?.invoke(strValue, selfRef, stackController)
                        }
                    }
                } else {
                    println("Incorrect input, repeating step")
                    selfRef.repeatSelf()
                }
            }
        }

        private fun checkStringInputValidity(
            input: String,
            multiValue: Boolean,
            associationMap: Map<Int, Pair<String, suspend (String, AbstractStep, IStackController?) -> Unit>>
        ): Boolean {
            val splittedInput = input.split(" ")

            if (!multiValue && splittedInput.size > 1) return false
            try {
                if (splittedInput
                        .map { it.toInt() }
                        .filter { it !in associationMap.keys }
                        .isNotEmpty()
                ) return false
            } catch (exc: NumberFormatException) {
                return false
            }
            return true
        }


        private fun formOptionAssociationMap(optBundle: StepOptionBundle): Map<Int, Pair<String,
                suspend (String, AbstractStep, IStackController?) -> Unit>> {
            TreeMap<Int, Pair<String,
                    suspend (String, AbstractStep, IStackController?) -> Unit>>().apply {
                put(0, GO_BACK_OPTION_LITERALL to defaultGoBackReaction)
                var counter = 1
                for (option in optBundle) {
                    put(counter++, option.visibleText to option.reaction)
                }
                return this
            }
        }

        val defaultGoBackReaction: suspend (
            receivedValue: String,
            selfRef: AbstractStep,
            stackController: IStackController?
        ) -> Unit = { value, selfRef, stackController ->
            selfRef.parentStep?.let {
                selfRef.addAncestorStep(it)
            }
        }


    }
}