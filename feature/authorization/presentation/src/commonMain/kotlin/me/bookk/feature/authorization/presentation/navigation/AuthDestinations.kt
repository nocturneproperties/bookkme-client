package me.bookk.feature.authorization.presentation.navigation

import me.bookk.core.presentation.navigation.NavigationDestinationDeclaration

object SignUpDestination : NavigationDestinationDeclaration() {
    override val route: String = "sign_up"
}

object SignInDestination : NavigationDestinationDeclaration() {
    override val route: String = "sign_in"
}

object TroubleshootDestination : NavigationDestinationDeclaration() {
    override val route: String = "troubleshoot"
}
