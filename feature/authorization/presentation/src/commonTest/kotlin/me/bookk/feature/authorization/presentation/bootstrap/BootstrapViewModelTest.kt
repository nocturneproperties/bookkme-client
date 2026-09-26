package me.bookk.feature.authorization.presentation.bootstrap

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.answering.calls
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.test.assertNothingMapped
import me.bookk.designsystem.test.assertSingle
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.feature.authorization.domain.api.GetSettingsColorScheme
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.api.IsUserLoggedIn
import me.bookk.feature.authorization.domain.api.LogOut
import me.bookk.feature.authorization.domain.api.InitiateBusinessSuspend
import me.bookk.feature.authorization.domain.entity.ColorScheme
import me.bookk.feature.authorization.presentation.FakeAuthStateFactory
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BootstrapViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture {
        val loggedIn = MutableStateFlow(false)
        val colorScheme = MutableStateFlow(ColorScheme.SYSTEM)
        val isUserLoggedIn = mock<IsUserLoggedIn> {
            every { asFlow() } returns loggedIn
        }
        val getSettingsColorScheme = mock<GetSettingsColorScheme> {
            every { asFlow() } returns colorScheme
        }
        val logOut = mock<LogOut>()
        val initialAppDataFetch = mock<InitialAppDataFetch> {
            everySuspend { timestampProtectedFetch() } returns Unit
        }

        val initiateBusinessSuspend = mock<InitiateBusinessSuspend> {
            everySuspend { invoke() } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = BootstrapViewModel(
            isUserLoggedIn = isUserLoggedIn,
            getSettingsColorScheme = getSettingsColorScheme,
            logOut = logOut,
            initialAppDataFetch = initialAppDataFetch,
            initiateBusinessSuspend = initiateBusinessSuspend,
            stateFactory = FakeAuthStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `starts at login when user is logged out`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(BootstrapNavigationDestination.Login, sut.state.startDestination)
    }

    @Test
    fun `switches to main when user logs in`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.loggedIn.value = true

        then()
        assertEquals(BootstrapNavigationDestination.Main, sut.state.startDestination)
    }

    @Test
    fun `applies color scheme changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.colorScheme.value = ColorScheme.DARK

        then()
        assertEquals(BootstrapState.UIColorScheme.DARK, sut.state.colorScheme)
    }

    @Test
    fun `runs throttled initial data fetch on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.initialAppDataFetch.timestampProtectedFetch() }
    }

    @Test
    fun `ignores initial data fetch failure`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.initialAppDataFetch.timestampProtectedFetch() } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(BootstrapNavigationDestination.Login, sut.state.startDestination)
    }

    @Test
    fun `logs out`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.logOut() } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.logOut()

        then()
        verifySuspend { fixture.logOut() }
    }

    @Test
    fun `tells the user their business access is suspended`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onBusinessAccessSuspended()

        then()
        val message = sut.state.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(DesignSystem.strings.error_access_suspended_title.desc(), message.title)
        assertEquals(DesignSystem.strings.error_access_suspended.desc(), message.message)
    }

    @Test
    fun `initiates the business suspension when business access is suspended`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.onBusinessAccessSuspended()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.initiateBusinessSuspend() }
    }

    @Test
    fun `ignores suspensions reported while the previous one is in progress`() = runUnitTest {
        given()
        val fixture = Fixture()
        val refresh = CompletableDeferred<Unit>()
        everySuspend { fixture.initiateBusinessSuspend() } calls { refresh.await() }
        val sut = fixture.sut()

        whenn()
        sut.onBusinessAccessSuspended()
        sut.onBusinessAccessSuspended()
        sut.onBusinessAccessSuspended()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.initiateBusinessSuspend() }
        sut.state.notifications.assertSingle<PresentationNotification.Message>()
        refresh.complete(Unit)
    }

    @Test
    fun `handles a suspension reported after the previous one finished`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onBusinessAccessSuspended()

        whenn()
        sut.onBusinessAccessSuspended()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.initiateBusinessSuspend() }
    }

    @Test
    fun `does not surface a failed business suspension`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.initiateBusinessSuspend() } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.onBusinessAccessSuspended()

        then()
        sut.state.notifications.assertSingle<PresentationNotification.Message>()
        fixture.errorMapper.assertNothingMapped()
    }
}
