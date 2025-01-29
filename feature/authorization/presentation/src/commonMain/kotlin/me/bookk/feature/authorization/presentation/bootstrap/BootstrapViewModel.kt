package me.bookk.feature.authorization.presentation.bootstrap

import me.bookk.core.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.authorization.domain.api.IsUserLoggedIn
import me.bookk.feature.authorization.presentation.AuthStateFactory

class BootstrapViewModel(
    private val isUserLoggedIn: IsUserLoggedIn,
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val state = stateFactory.createBootstrapState()

    init {
        launch(
            launchIn = DispatcherProvider.io,
            call = { isUserLoggedIn.invoke() },
            onComplete = { isLoggedIn ->
                state.navigation.navigationDestination = if (isLoggedIn) {
                    BootstrapNavigationDestination.ToMain
                } else {
                    BootstrapNavigationDestination.ToLogin
                }
            }
        )
    }
}