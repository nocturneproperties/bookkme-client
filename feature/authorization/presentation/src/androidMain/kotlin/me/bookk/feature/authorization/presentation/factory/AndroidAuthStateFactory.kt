package me.bookk.feature.authorization.presentation.factory

import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.bootstrap.AndroidBootstrapState
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState
import me.bookk.feature.authorization.presentation.sign_in.AndroidSignInState
import me.bookk.feature.authorization.presentation.sign_in.state.SignInState
import me.bookk.feature.authorization.presentation.sign_up.AndroidSignUpState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState
import me.bookk.feature.authorization.presentation.troubleshoot.AndroidTroubleshootState
import me.bookk.feature.authorization.presentation.troubleshoot.state.TroubleshootState

class AndroidAuthStateFactory : AuthStateFactory {
    override fun createSignUpState(initData: SignUpState.InitData): SignUpState {
        return AndroidSignUpState(initData)
    }

    override fun createSignInState(initData: SignInState.InitData): SignInState {
        return AndroidSignInState(initData)
    }

    override fun createBootstrapState(): BootstrapState {
        return AndroidBootstrapState()
    }

    override fun createTroubleshootState(initData: TroubleshootState.InitData): TroubleshootState {
        return AndroidTroubleshootState(initData)
    }
}