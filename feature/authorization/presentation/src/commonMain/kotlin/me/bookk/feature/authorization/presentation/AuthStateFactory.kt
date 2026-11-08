package me.bookk.feature.authorization.presentation

import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState
import me.bookk.feature.authorization.presentation.screen.changeemail.ChangeEmailState
import me.bookk.feature.authorization.presentation.screen.verifyemail.VerifyEmailState
import me.bookk.feature.authorization.presentation.sign_in.state.SignInState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState
import me.bookk.feature.authorization.presentation.troubleshoot.state.TroubleshootState

interface AuthStateFactory {
    fun createSignUpState(): SignUpState
    fun createSignUpLegalDocumentState(): SignUpLegalDocumentState
    fun createSignInState(initData: SignInState.InitData): SignInState
    fun createTroubleshootState(initData: TroubleshootState.InitData): TroubleshootState
    fun createBootstrapState(): BootstrapState
    fun createVerifyEmailState(): VerifyEmailState
    fun createChangeEmailState(): ChangeEmailState
}
