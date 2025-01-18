package me.bookk.feature.authorization.presentation.sign_in

sealed interface SignInNavigationEvent {
    data object ToMain : SignInNavigationEvent
}