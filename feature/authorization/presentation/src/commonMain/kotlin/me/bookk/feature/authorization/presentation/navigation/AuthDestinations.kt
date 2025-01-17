package me.bookk.feature.authorization.presentation.navigation

import me.bookk.core.presentation.NavigationDestination

object SignUpDestination : NavigationDestination() {
    override val route: String = "sign_up"
}

object SignInDestination : NavigationDestination() {
    override val route: String = "sign_in"
}
