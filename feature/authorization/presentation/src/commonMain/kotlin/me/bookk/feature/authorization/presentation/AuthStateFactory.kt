package me.bookk.feature.authorization.presentation

import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

interface AuthStateFactory {
    fun createSignUpState(initData: SignUpState.InitData): SignUpState
}