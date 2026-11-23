package me.bookk.feature.authorization.presentation.screen.clientbootstrap

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertNothingMapped
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.api.IsUserLoggedIn
import me.bookk.feature.authorization.domain.api.LogOut
import me.bookk.feature.authorization.presentation.FakeClientStateFactory
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapNavigationDestination
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientBootstrapViewModelTest {

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
        val isUserLoggedIn = mock<IsUserLoggedIn> {
            every { asFlow() } returns loggedIn
        }
        val initialAppDataFetch = mock<InitialAppDataFetch> {
            everySuspend { timestampProtectedFetch() } returns Unit
        }
        val logOut = mock<LogOut> {
            everySuspend { invoke() } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut(): ClientBootstrapViewModel {
            return ClientBootstrapViewModel(
                isUserLoggedIn = isUserLoggedIn,
                initialAppDataFetch = initialAppDataFetch,
                logOut = logOut,
                stateFactory = FakeClientStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }
    }

    @Test
    fun `starts at login when the user is signed out`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(BootstrapNavigationDestination.Login, sut.uiState.startDestination)
    }

    @Test
    fun `starts at main when the user is already signed in`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.loggedIn.value = true

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(BootstrapNavigationDestination.Main, sut.uiState.startDestination)
    }

    @Test
    fun `switches to main once the user signs in`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.loggedIn.value = true

        then()
        assertEquals(BootstrapNavigationDestination.Main, sut.uiState.startDestination)
    }

    @Test
    fun `returns to login when the user is signed out`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.loggedIn.value = true
        val sut = fixture.sut()

        whenn()
        fixture.loggedIn.value = false

        then()
        assertEquals(BootstrapNavigationDestination.Login, sut.uiState.startDestination)
    }

    @Test
    fun `runs the throttled initial data fetch on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.initialAppDataFetch.timestampProtectedFetch() }
    }

    @Test
    fun `does not surface an initial data fetch failure`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.initialAppDataFetch.timestampProtectedFetch() } throws
            TestException()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(BootstrapNavigationDestination.Login, sut.uiState.startDestination)
        fixture.errorMapper.assertNothingMapped()
    }

    @Test
    fun `logs the user out when the session is no longer authorized`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.logOut()

        then()
        verifySuspend { fixture.logOut() }
    }

    @Test
    fun `does not surface a log out failure`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.logOut() } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.logOut()

        then()
        fixture.errorMapper.assertNothingMapped()
    }
}
