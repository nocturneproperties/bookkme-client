package me.bookk.feature.authorization.presentation.sign_in

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import library.device.api.DeviceFacade
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.feature.authorization.domain.api.SignIn
import me.bookk.feature.authorization.presentation.FakeAuthStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SignInViewModelTest {

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
        val signIn = mock<SignIn>()
        val deviceFacade = mock<DeviceFacade> {
            every { openUrlPreview(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = SignInViewModel(
            signIn = signIn,
            deviceFacade = deviceFacade,
            stateFactory = FakeAuthStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `signs in and stops loading`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.signIn() } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.onSignInClick()

        then()
        verifySuspend { fixture.signIn() }
        assertTrue(sut.uiState.notification.presentationNotification.isEmpty())
        assertFalse(sut.uiState.signInButton.isLoading)
    }

    @Test
    fun `shows message when passkey has no account`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.signIn() } throws SignIn.Error.NoAccountForThisPasskey()
        val sut = fixture.sut()

        whenn()
        sut.onSignInClick()

        then()
        sut.uiState.notification.assertSingle<PresentationNotification.Message>()
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows message when passkey verification fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.signIn() } throws SignIn.Error.PasskeyVerificationFailed()
        val sut = fixture.sut()

        whenn()
        sut.onSignInClick()

        then()
        sut.uiState.notification.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows message when device has no passkeys`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.signIn() } throws SignIn.Error.NoCredentialsAvailable()
        val sut = fixture.sut()

        whenn()
        sut.onSignInClick()

        then()
        sut.uiState.notification.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows mapped error when sign in fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.signIn() } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.onSignInClick()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.signInButton.isLoading)
    }

    @Test
    fun `opens passkey info on learn more`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.onLearnMoreClick()

        then()
        verify { fixture.deviceFacade.openUrlPreview(any()) }
    }

    @Test
    fun `pushes main destination on back`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onBackClick()

        then()
        assertEquals(listOf<SignInNavigationDestination>(SignInNavigationDestination.Main), sut.uiState.navigation.navigationDestination)
    }
}
