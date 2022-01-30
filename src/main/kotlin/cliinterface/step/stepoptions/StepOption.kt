package cliinterface.step.stepoptions


import cliinterface.stack.IStackController
import cliinterface.step.AbstractStep

data class StepOption(val idName:String,
                      val visibleText:String,
                      val  reaction:suspend (receivedValue:String,
                                              selfRef:AbstractStep,
                                              stackController:IStackController?) -> Unit) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StepOption

        if (idName != other.idName) return false
        if (visibleText != other.visibleText) return false

        return true
    }

    override fun hashCode(): Int {
        var result = idName.hashCode()
        result = 31 * result + visibleText.hashCode()
        return result
    }

    class StepOtionBuilder{
        var idName:String?=null
        var visibleText:String?=null
        var  reaction: (suspend (receivedValue:String,
                               selfRef:AbstractStep,
                               stackController:IStackController?) -> Unit)?=null

    fun build():StepOption{
        return  StepOption(idName!!,visibleText!!,reaction!!)
    }

    }



}