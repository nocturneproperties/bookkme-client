package me.bookk.feature.authorization.presentation.bootstrap

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.core.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.authorization.domain.api.IsUserLoggedIn
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState.UIColorScheme
import me.bookk.feature.settings.domain.api.GetColorScheme

class BootstrapViewModel(
    private val isUserLoggedIn: IsUserLoggedIn,
    private val getColorScheme: GetColorScheme,
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val state = stateFactory.createBootstrapState()

    init {
        loadStartupInfo()
        observeThemeUpdates()
    }

    private fun loadStartupInfo() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { isUserLoggedIn() to getColorScheme() },
            onComplete = { (isLoggedIn , scheme) ->
                state.colorScheme =  UIColorScheme.from(scheme)
                state.navigation.navigationDestination = if (isLoggedIn) {
                    BootstrapNavigationDestination.ToMain
                } else {
                    BootstrapNavigationDestination.ToMain
                }
            }
        )
    }

    private fun observeThemeUpdates() {
        getColorScheme.asFlow()
            .distinctUntilChanged()
            .onEach { state.colorScheme = UIColorScheme.from(it) }
            .launchIn(viewModelScope)
    }
}