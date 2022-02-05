package cliinterface.properties

import cliinterface.step.AbstractStep
import kotlin.reflect.KClass


data class SimpleProperty<T:Any>(
     override val name:String,
     override val authorStep: AbstractStep?,
     override val value: T,
     override val type: KClass<T>
   ):AbstractProperty<T>{
     override fun equals(other: Any?): Boolean {
         if (this === other) return true
         if (javaClass != other?.javaClass) return false

         other as SimpleProperty<*>

         if (name != other.name) return false

         return true
     }

     override fun hashCode(): Int {
         return name.hashCode()
     }
 }