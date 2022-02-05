package cliinterface.properties

import cliinterface.step.AbstractStep
import kotlin.reflect.KClass

interface AbstractProperty <T:Any> {
    val name:String
    val authorStep:AbstractStep?
    val authorAdress:List<String>
        get() = authorStep?.adress?: listOf(name)
    val value:T
    val type:KClass<T>
}


fun AbstractProperty<*>.isAuthoredByStepOrDescendant(checkStep: AbstractStep):Boolean{
    return authorAdress.contains(checkStep.name)
}
fun AbstractProperty<*>.isAuthoredByStepOrDescendant(checkStepName: String):Boolean{
    return authorAdress.contains(checkStepName)
}