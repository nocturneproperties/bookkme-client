package me.bookk.feature.authorization.presentation.factory

import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.bootstrap.AndroidBootstrapState
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState
import me.bookk.feature.authorization.presentation.screen.changeemail.AndroidChangeEmailState
import me.bookk.feature.authorization.presentation.screen.changeemail.ChangeEmailState
import me.bookk.feature.authorization.presentation.screen.verifyemail.AndroidVerifyEmailState
import me.bookk.feature.authorization.presentation.screen.verifyemail.VerifyEmailState
import me.bookk.feature.authorization.presentation.sign_in.AndroidSignInState
import me.bookk.feature.authorization.presentation.sign_in.state.SignInState
import me.bookk.feature.authorization.presentation.sign_up.AndroidSignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.sign_up.AndroidSignUpState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpLegalDocumentState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState
import me.bookk.feature.authorization.presentation.troubleshoot.AndroidTroubleshootState
import me.bookk.feature.authorization.presentation.troubleshoot.state.TroubleshootState

class AndroidAuthStateFactory : AuthStateFactory {
    override fun createSignUpState(): SignUpState {
        return AndroidSignUpState()
    }

    override fun createSignUpLegalDocumentState(): SignUpLegalDocumentState {
        return AndroidSignUpLegalDocumentState()
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

    override fun createVerifyEmailState(): VerifyEmailState {
        return AndroidVerifyEmailState()
    }

    override fun createChangeEmailState(): ChangeEmailState {
        return AndroidChangeEmailState()
    }
}
