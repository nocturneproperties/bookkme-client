package me.bookk.feature.authorization.presentation.sign_up

sealed interface SignUpNavigationEvent {
    data object ToPasskey : SignUpNavigationEvent
    data object ToMain : SignUpNavigationEvent
}