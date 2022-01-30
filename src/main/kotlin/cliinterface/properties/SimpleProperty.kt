package cliinterface.properties

import cliinterface.step.AbstractStep


 data class SimpleProperty<T>(
     override val name:String,
     override val authorStep: AbstractStep?,
     override val value: T
   ):AbstractPropety<T>{
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