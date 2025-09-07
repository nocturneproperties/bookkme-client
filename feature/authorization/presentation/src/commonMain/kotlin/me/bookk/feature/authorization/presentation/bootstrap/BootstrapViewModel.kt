package me.bookk.feature.authorization.presentation.bootstrap

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.authorization.domain.api.IsUserLoggedIn
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState.UIColorScheme
import me.bookk.feature.settings.domain.api.GetColorScheme
import me.bookk.feature.settings.domain.api.LogOut

class BootstrapViewModel(
    private val isUserLoggedIn: IsUserLoggedIn,
    private val getColorScheme: GetColorScheme,
    private val logOut: LogOut,
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val state = stateFactory.createBootstrapState()

    init {
        observeAuthorizationStatus()
        observeThemeUpdates()
    }

    fun logOut() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { logOut.invoke() }
        )
    }

    private fun observeAuthorizationStatus() {
        isUserLoggedIn.asFlow()
            .distinctUntilChanged()
            .flowOn(DispatcherProvider.io)
            .onEach { isLoggedIn ->
                state.startDestination = if (!isLoggedIn) {
                    BootstrapNavigationDestination.Main
                } else {
                    BootstrapNavigationDestination.Login
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeThemeUpdates() {
        getColorScheme.asFlow()
            .distinctUntilChanged()
            .flowOn(DispatcherProvider.io)
            .onEach { state.colorScheme = UIColorScheme.from(it) }
            .launchIn(viewModelScope)
    }
}