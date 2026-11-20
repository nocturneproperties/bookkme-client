package me.bookk.feature.authorization.presentation.screen.clientbootstrap

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.api.IsUserLoggedIn
import me.bookk.feature.authorization.presentation.ClientStateFactory
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapNavigationDestination

class ClientBootstrapViewModel(
    private val isUserLoggedIn: IsUserLoggedIn,
    private val initialAppDataFetch: InitialAppDataFetch,
    stateFactory: ClientStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: ClientBootstrapState = stateFactory.createClientBootstrapState()

    init {
        observeAuthorizationStatus()
        fetchInitialData()
    }

    private fun observeAuthorizationStatus() {
        isUserLoggedIn.asFlow()
            .distinctUntilChanged()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { uiState.startDestination = startDestination(it) }
            .observe()
    }

    private fun startDestination(isLoggedIn: Boolean): BootstrapNavigationDestination {
        return if (isLoggedIn) {
            BootstrapNavigationDestination.Main
        } else {
            BootstrapNavigationDestination.Login
        }
    }

    private fun fetchInitialData() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { initialAppDataFetch.timestampProtectedFetch() },
            onError = {},
        )
    }
}
