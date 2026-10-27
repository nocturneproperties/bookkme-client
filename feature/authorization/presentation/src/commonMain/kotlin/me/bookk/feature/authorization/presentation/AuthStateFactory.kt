package me.bookk.feature.authorization.presentation

import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState
import me.bookk.feature.authorization.presentation.sign_in.state.SignInState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState
import me.bookk.feature.authorization.presentation.troubleshoot.state.TroubleshootState

interface AuthStateFactory {
    fun createSignUpState(initData: SignUpState.InitData): SignUpState
    fun createSignInState(initData: SignInState.InitData): SignInState
    fun createTroubleshootState(initData: TroubleshootState.InitData): TroubleshootState
    fun createBootstrapState(): BootstrapState
}