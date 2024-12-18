package me.bookk.feature.authorization.presentation.sign_up

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableSharedFlow
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpEventListener
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

class SignUpViewModel(
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs), SignUpEventListener {

    val uiState = stateFactory.createSignUpState(createInitData())
    val navigationFlow = MutableSharedFlow<SignUpNavigationEvent>()

    companion object {
        fun createInitData() = SignUpState.InitData(
            nameHint = "".desc(),
            lastNameHint = "".desc(),
            emailHint = "".desc(),
            phoneHint = "".desc(),
            businessNameHint = "".desc(),
            confirmButtonText = "".desc()
        )
    }
}