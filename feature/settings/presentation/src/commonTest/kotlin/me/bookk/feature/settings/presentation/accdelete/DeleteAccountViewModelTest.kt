package me.bookk.feature.settings.presentation.accdelete

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import me.bookk.android.feature.settings.resources.SettingsRes
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
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.settings.domain.api.DeleteAccount
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import me.bookk.feature.settings.presentation.accdelete.DeleteAccountNavigationDestination.Back
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeleteAccountViewModelTest {

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
        val deleteAccount = mock<DeleteAccount>()
        val errorMapper = FakeErrorMapper()

        fun sut() = DeleteAccountViewModel(
            deleteAccount = deleteAccount,
            settingsStateFactory = FakeSettingsStateFactory(),
            vmArgs = VmArgs(errorMapper),
        )

        fun agreedSut() = sut().apply { uiState.confirmation.onCheckedChange?.invoke(true) }
    }

    @Test
    fun `shows the warning, the agreement and the delete action`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        val state = sut.uiState
        assertEquals(SettingsRes.strings.settings_delete_account_title.desc(), state.appBar.title)
        assertEquals(TopBarSize.LARGE, state.appBar.size)
        assertEquals(
            SettingsRes.strings.settings_delete_account_confirmation.desc(),
            state.confirmationMessage,
        )
        assertEquals(
            SettingsRes.strings.settings_delete_account_confirmation_switch.desc(),
            state.confirmation.text,
        )
        assertEquals(
            SettingsRes.strings.settings_delete_account_confirmation_button.desc(),
            state.deleteButton.text,
        )
    }

    @Test
    fun `keeps delete disabled until the agreement is checked`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.confirmation.isChecked)
        assertFalse(sut.uiState.deleteButton.isEnabled)
    }

    @Test
    fun `goes back when back is pressed`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf(Back), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `enables delete only after confirmation is checked`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.confirmation.onCheckedChange?.invoke(true)

        then()
        assertTrue(sut.uiState.confirmation.isChecked)
        assertTrue(sut.uiState.deleteButton.isEnabled)
    }

    @Test
    fun `disables delete when confirmation is unchecked`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.uiState.confirmation.onCheckedChange?.invoke(true)

        whenn()
        sut.uiState.confirmation.onCheckedChange?.invoke(false)

        then()
        assertFalse(sut.uiState.confirmation.isChecked)
        assertFalse(sut.uiState.deleteButton.isEnabled)
    }

    @Test
    fun `deletes account and shows success message`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } returns Unit
        val sut = fixture.agreedSut()

        whenn()
        sut.uiState.deleteButton.onClick?.invoke()

        then()
        verifySuspend { fixture.deleteAccount() }
        sut.uiState.notifications.presentationNotification
            .filterIsInstance<PresentationNotification.GlobalMessage>()
            .single()
        assertFalse(sut.uiState.deleteButton.isLoading)
    }

    @Test
    fun `plays success haptic when account is deleted`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } returns Unit
        val sut = fixture.agreedSut()

        whenn()
        sut.uiState.deleteButton.onClick?.invoke()

        then()
        assertEquals(
            1,
            sut.uiState.notifications.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `does not play success haptic when account deletion fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } throws TestException()
        val sut = fixture.agreedSut()

        whenn()
        sut.uiState.deleteButton.onClick?.invoke()

        then()
        assertTrue(
            sut.uiState.notifications.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `shows mapped error when deletion fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } throws TestException()
        val sut = fixture.agreedSut()

        whenn()
        sut.uiState.deleteButton.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.deleteButton.isLoading)
    }

    @Test
    fun `lets the agreed user retry after deletion fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } throws TestException()
        val sut = fixture.agreedSut()

        whenn()
        sut.uiState.deleteButton.onClick?.invoke()

        then()
        assertTrue(sut.uiState.deleteButton.isEnabled)
    }
}
