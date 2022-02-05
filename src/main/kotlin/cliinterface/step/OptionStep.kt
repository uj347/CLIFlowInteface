package cliinterface.step


import cliinterface.properties.AbstractProperty
import cliinterface.stack.IStackController
import cliinterface.step.stepoptions.StepOptionBundle
import kotlinx.coroutines.flow.FlowCollector
import java.util.*

class OptionStep private constructor(
    name:String,
    parentStep: AbstractStep?,
    multiOptional:Boolean,
    associationMap: Map<Int, Pair<String, suspend (String, AbstractStep, IStackController?) -> Unit>>,
    layoutBlock:suspend (MutableSet<AbstractProperty<*>>?)->Unit,
    inputBlock:suspend (Unit)->String,
    processingBlock:suspend (String, AbstractStep, IStackController?) -> Unit,
    onCompletionBlock: suspend FlowCollector<Unit>.(Throwable?) -> Unit= { emptyCompletionBlock }
) : SimpleStep(
name,parentStep,layoutBlock,inputBlock,processingBlock,onCompletionBlock
) {

companion object{
    fun create(
        name: String,
        parentStep: AbstractStep?,
        multiOptional: Boolean,
        message:String,
        optionBundle: StepOptionBundle
    ):OptionStep{
        val associationMap= formOptionAssociationMap(optionBundle)
        val layoutBlock= generateLayoutBlock(associationMap, message)
        val processingBlock= generateProcessingBlock(associationMap,multiOptional)
        return OptionStep(name,
            parentStep,
            multiOptional,
            associationMap,
            layoutBlock,
            { oneLineCliInputBlock() },
            processingBlock)
    }

    const val GO_BACK_OPTION_LITERALL="Back"

    private  fun generateLayoutBlock (associationMap:Map<Int,Pair<String,
            suspend (String, AbstractStep, IStackController?) -> Unit>>, message: String):suspend (MutableSet<AbstractProperty<*>>?)->Unit{
       return {stateToken->
           println(message)
           for (entry in associationMap) {
               println("${entry.key}. ${entry.value.first}")
           }
       }
    }

    private fun generateProcessingBlock(associationMap: Map<Int, Pair<String,
            suspend (String, AbstractStep, IStackController?) -> Unit>>, multiOptional:Boolean)
    :suspend (String, AbstractStep, IStackController?) -> Unit{
        return {strValue,selfRef,stackController->
            if (checkStringInputValidity(strValue,multiOptional)){
                val intInput=strValue.split(" ").map{it.toInt()}
                when{
                    intInput.contains(0)->associationMap.get(0)?.second?.invoke(strValue,selfRef,stackController)
                    else->{
                        for (integer in intInput){
                            associationMap.get(integer)?.second?.invoke(strValue,selfRef,stackController)
                        }
                    }
                }
            }else{
                println("Incorrect input, repeating step")
                selfRef.repeatSelf()
            }
        }
    }

    private fun checkStringInputValidity(input: String,multiValue:Boolean):Boolean{
        val splittedInput=input.split(" ")

        if(!multiValue&&splittedInput.size>1) return false
        try{
            for (value in splittedInput){
                value.toInt()
            }
        } catch (exc:NumberFormatException){
            return false
        }
        return true
    }



    private  fun formOptionAssociationMap(optBundle: StepOptionBundle):Map<Int,Pair<String,
               suspend (String, AbstractStep, IStackController?) -> Unit>>{
        TreeMap<Int,Pair<String,
                suspend (String, AbstractStep, IStackController?) -> Unit>>().apply {
            put(0, GO_BACK_OPTION_LITERALL to defaultGoBackReaction)
            var counter=1
            for(option in optBundle){
              put(counter++,option.visibleText to option.reaction)
            }
            return this
        }
    }

    val defaultGoBackReaction:suspend (receivedValue:String,
                                       selfRef: AbstractStep,
                                       stackController: IStackController?) -> Unit = { value, selfRef, stackController->
    selfRef.parentStep?.let{
        selfRef.addAncestorStep(it)
    }
                               }



}
}