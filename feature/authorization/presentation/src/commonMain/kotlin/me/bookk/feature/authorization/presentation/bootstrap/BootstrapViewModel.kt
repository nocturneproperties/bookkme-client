package me.bookk.feature.authorization.presentation.bootstrap

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.feature.authorization.domain.api.GetSettingsColorScheme
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.api.IsUserLoggedIn
import me.bookk.feature.authorization.domain.api.LogOut
import me.bookk.feature.authorization.domain.api.InitiateBusinessSuspend
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState.UIColorScheme

class BootstrapViewModel(
    private val isUserLoggedIn: IsUserLoggedIn,
    private val getSettingsColorScheme: GetSettingsColorScheme,
    private val logOut: LogOut,
    private val initialAppDataFetch: InitialAppDataFetch,
    private val initiateBusinessSuspend: InitiateBusinessSuspend,
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val state = stateFactory.createBootstrapState()

    init {
        observeAuthorizationStatus()
        observeThemeUpdates()
        fetchInitialData()
    }

    fun logOut() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { logOut.invoke() },
            onError = { /*NOOP*/ }
        )
    }

    fun onBusinessAccessSuspended() {
        launch(
            key = BUSINESS_ACCESS_SUSPENDED_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { state.notifications.add(businessAccessSuspendedMessage()) },
            call = { initiateBusinessSuspend() },
            onError = { handleError(it) }
        )
    }

    private fun businessAccessSuspendedMessage(): PresentationNotification.Message {
        return PresentationNotification.Message(
            title = DesignSystem.strings.error_access_suspended_title.desc(),
            message = DesignSystem.strings.error_access_suspended.desc(),
            buttons = listOf(ButtonDescriptor(text = DesignSystem.strings.action_ok.desc()))
        )
    }

    private fun fetchInitialData() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { initialAppDataFetch.timestampProtectedFetch() },
            onError = { /*NOOP*/ }
        )
    }

    private fun observeAuthorizationStatus() {
        isUserLoggedIn.asFlow()
            .distinctUntilChanged()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { isLoggedIn ->
                state.startDestination = if (isLoggedIn) {
                    BootstrapNavigationDestination.Main
                } else {
                    BootstrapNavigationDestination.Login
                }
            }
            .observe()
    }

    private fun observeThemeUpdates() {
        getSettingsColorScheme.asFlow()
            .distinctUntilChanged()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { state.colorScheme = UIColorScheme.from(it) }
            .observe()
    }

    private companion object {
        const val BUSINESS_ACCESS_SUSPENDED_KEY = "business_access_suspended"
    }
}
