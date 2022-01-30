package cliinterface

import cliinterface.properties.AbstractPropety
import cliinterface.stack.DaggerStackController
import cliinterface.step.AbstractStep
import dagger.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
interface CLIModule{
        @Singleton
        @StateToken
        @Binds
        fun providePropertiesToken(primerSet:MutableSet<AbstractPropety<*>>):MutableSet<AbstractPropety<*>>


    @AssistedFactory
    interface RunStackControllerFactory{
        fun buildController(@Assisted(FIRST_STEP_LITERAL) firstStep: AbstractStep,
        @Assisted(TERMINAL_STEP_LITERAL) terminalStep: AbstractStep?):DaggerStackController
    }


}
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class StateToken

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ControllerContext


const val FIRST_STEP_LITERAL="FirstStep"
const val TERMINAL_STEP_LITERAL="TerminalStep"

