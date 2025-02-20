package me.bookk.feature.authorization.presentation.bootstrap.state

import me.bookk.feature.authorization.presentation.bootstrap.BootstrapNavigationDestination
import me.bookk.feature.settings.domain.api.entity.ColorScheme

interface BootstrapState {
    var colorScheme: UIColorScheme
    var startDestination: BootstrapNavigationDestination?

    //UI model to prevent need of Settings Domain API module to be exported to objc header
    enum class UIColorScheme {
        DARK,
        LIGHT,
        SYSTEM;

        companion object {
            internal fun from(domain: ColorScheme): UIColorScheme {
                return when(domain) {
                    ColorScheme.DARK -> DARK
                    ColorScheme.LIGHT -> LIGHT
                    ColorScheme.SYSTEM -> SYSTEM
                }
            }
        }
    }
}