package me.bookk.feature.authorization.presentation.factory

import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.sign_up.AndroidSignUpState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

class AndroidAuthStateFactory : AuthStateFactory {
    override fun createSignUpState(initData: SignUpState.InitData): SignUpState {
        return AndroidSignUpState(initData)
    }
}