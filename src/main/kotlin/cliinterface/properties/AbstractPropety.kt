package cliinterface.properties

import cliinterface.step.AbstractStep

interface AbstractPropety <T> {
    val name:String
    val authorStep:AbstractStep?
    val authorAdress:List<String>
        get() = authorStep?.adress?: listOf(name)
    val value:T
}


fun AbstractPropety<*>.isAuthoredByStepOrDescendant(checkStep: AbstractStep):Boolean{
    return authorAdress.contains(checkStep.name)
}
fun AbstractPropety<*>.isAuthoredByStepOrDescendant(checkStepName: String):Boolean{
    return authorAdress.contains(checkStepName)
}