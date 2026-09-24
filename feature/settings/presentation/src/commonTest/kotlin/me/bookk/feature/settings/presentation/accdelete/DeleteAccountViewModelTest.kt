package me.bookk.feature.settings.presentation.accdelete

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
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
import me.bookk.feature.settings.domain.api.DeleteAccount
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
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
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `enables delete only after confirmation is checked`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onSwitchStateChanged(true)

        then()
        assertTrue(sut.uiState.confirmation.isChecked)
        assertTrue(sut.uiState.deleteButton.isEnabled)
    }

    @Test
    fun `disables delete when confirmation is unchecked`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.onSwitchStateChanged(true)

        whenn()
        sut.onSwitchStateChanged(false)

        then()
        assertFalse(sut.uiState.deleteButton.isEnabled)
    }

    @Test
    fun `deletes account and shows success message`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.onDeleteClick()

        then()
        verifySuspend { fixture.deleteAccount() }
        sut.uiState.notifications.assertSingle<PresentationNotification.GlobalMessage>()
        assertFalse(sut.uiState.deleteButton.isLoading)
    }

    @Test
    fun `shows mapped error when deletion fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.onDeleteClick()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.deleteButton.isLoading)
    }
}
