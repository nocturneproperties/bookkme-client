package me.bookk.feature.settings.presentation.accdelete

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentially
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.settings.domain.api.DeleteAccount
import me.bookk.feature.settings.domain.api.GetAccountDeletion
import me.bookk.feature.settings.domain.api.entity.ActionOutcome
import me.bookk.feature.settings.domain.api.entity.ChatConfirmationError
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import me.bookk.feature.settings.presentation.accdelete.DeleteAccountNavigationDestination.Back
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

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
        val deletion = MutableStateFlow<Instant?>(null)
        val getAccountDeletion = mock<GetAccountDeletion> {
            every { flow() } returns deletion
            everySuspend { refresh() } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = DeleteAccountViewModel(
            deleteAccount = deleteAccount,
            getAccountDeletion = getAccountDeletion,
            dateLocalizer = FakeDateLocalizer(),
            settingsStateFactory = FakeSettingsStateFactory(),
            vmArgs = VmArgs(errorMapper),
        )

        fun agreedSut() = sut().apply { uiState.confirmation.onCheckedChange?.invoke(true) }

        fun sentSut(): DeleteAccountViewModel {
            everySuspend { deleteAccount() } returns ActionOutcome.ConfirmationSent
            return agreedSut().apply { uiState.deleteButton.onClick?.invoke() }
        }
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
    fun `requests the deletion without a popup`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } returns ActionOutcome.Completed(Unit)
        val sut = fixture.agreedSut()

        whenn()
        sut.uiState.deleteButton.onClick?.invoke()

        then()
        verifySuspend { fixture.deleteAccount() }
        assertTrue(
            sut.uiState.notifications.presentationNotification.none {
                it is PresentationNotification.GlobalMessage
            },
        )
        assertFalse(sut.uiState.deleteButton.isLoading)
    }

    @Test
    fun `shows when the requested deletion runs`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.deletion.value = DELETES_AT

        then()
        assertEquals(
            SettingsRes.strings.settings_delete_account_pending.format(
                DELETES_AT.toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
            ),
            sut.uiState.pendingDeletion,
        )
    }

    @Test
    fun `hides the agreement and delete while a deletion is pending`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.deletion.value = DELETES_AT

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.confirmation.isVisible)
        assertFalse(sut.uiState.deleteButton.isVisible)
    }

    @Test
    fun `offers the agreement and delete when no deletion is pending`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        fixture.deletion.value = DELETES_AT

        whenn()
        fixture.deletion.value = null

        then()
        assertNull(sut.uiState.pendingDeletion)
        assertTrue(sut.uiState.confirmation.isVisible)
        assertTrue(sut.uiState.deleteButton.isVisible)
    }

    @Test
    fun `checks whether a deletion is pending when opened`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getAccountDeletion.refresh() }
    }

    @Test
    fun `keeps the known deletion quietly when the check fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.deletion.value = DELETES_AT
        everySuspend { fixture.getAccountDeletion.refresh() } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.deleteButton.isVisible)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `plays success haptic when account is deleted`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } returns ActionOutcome.Completed(Unit)
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

    @Test
    fun `shows that the confirmation was sent to the telegram chat`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } returns ActionOutcome.ConfirmationSent
        val sut = fixture.agreedSut()

        whenn()
        sut.uiState.deleteButton.onClick?.invoke()

        then()
        assertEquals(
            SettingsRes.strings.settings_chat_confirmation_sent.desc(),
            sut.uiState.chatConfirmation.notice,
        )
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `waits for the cooldown before offering to send again`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sentSut()

        then()
        assertEquals(
            SettingsRes.strings.settings_chat_confirmation_send_again.desc(),
            sut.uiState.deleteButton.text,
        )
        assertFalse(sut.uiState.deleteButton.isLoading)
        assertFalse(sut.uiState.deleteButton.isEnabled)
    }

    @Test
    fun `keeps send again disabled until the cooldown ends`() = runUnitTest {
        given()
        val sut = Fixture().sentSut()

        whenn()
        advanceTimeBy(59.seconds)

        then()
        assertFalse(sut.uiState.deleteButton.isEnabled)
    }

    @Test
    fun `offers to send again once the cooldown ends`() = runUnitTest {
        given()
        val sut = Fixture().sentSut()

        whenn()
        advanceTimeBy(60.seconds)
        runCurrent()

        then()
        assertTrue(sut.uiState.deleteButton.isEnabled)
    }

    @Test
    fun `keeps send again disabled during the cooldown when the agreement is ticked again`() =
        runUnitTest {
            given()
            val sut = Fixture().sentSut()
            sut.uiState.confirmation.onCheckedChange?.invoke(false)

            whenn()
            sut.uiState.confirmation.onCheckedChange?.invoke(true)

            then()
            assertFalse(sut.uiState.deleteButton.isEnabled)
        }

    @Test
    fun `does not offer send again after the cooldown while the agreement is unticked`() =
        runUnitTest {
            given()
            val sut = Fixture().sentSut()
            sut.uiState.confirmation.onCheckedChange?.invoke(false)

            whenn()
            advanceTimeBy(60.seconds)
            runCurrent()

            then()
            assertFalse(sut.uiState.deleteButton.isEnabled)
        }

    @Test
    fun `sends the confirmation again when send again is tapped`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sentSut()
        advanceTimeBy(60.seconds)
        runCurrent()

        whenn()
        sut.uiState.deleteButton.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.deleteAccount() }
        assertFalse(sut.uiState.deleteButton.isEnabled)
    }

    @Test
    fun `treats a confirmation sent moments ago as sent`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } throws ChatConfirmationError.TooFrequent()
        val sut = fixture.agreedSut()

        whenn()
        sut.uiState.deleteButton.onClick?.invoke()

        then()
        assertEquals(
            SettingsRes.strings.settings_chat_confirmation_sent.desc(),
            sut.uiState.chatConfirmation.notice,
        )
        assertFalse(sut.uiState.deleteButton.isEnabled)
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `asks telegram for write access when the bot cannot write`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } throws ChatConfirmationError.WriteAccessRequired()
        val sut = fixture.agreedSut()

        whenn()
        sut.uiState.deleteButton.onClick?.invoke()

        then()
        assertTrue(sut.uiState.chatConfirmation.isWriteAccessRequested)
        assertNull(sut.uiState.chatConfirmation.notice)
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `sends the confirmation again once write access is granted`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } sequentially {
            throws(ChatConfirmationError.WriteAccessRequired())
            returns(ActionOutcome.ConfirmationSent)
        }
        val sut = fixture.agreedSut()
        sut.uiState.deleteButton.onClick?.invoke()

        whenn()
        sut.uiState.chatConfirmation.onWriteAccessAnswered(true)

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.deleteAccount() }
        assertFalse(sut.uiState.chatConfirmation.isWriteAccessRequested)
        assertEquals(
            SettingsRes.strings.settings_chat_confirmation_sent.desc(),
            sut.uiState.chatConfirmation.notice,
        )
    }

    @Test
    fun `explains that confirmation needs a bot message when write access is refused`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            everySuspend { fixture.deleteAccount() } throws
                ChatConfirmationError.WriteAccessRequired()
            val sut = fixture.agreedSut()
            sut.uiState.deleteButton.onClick?.invoke()

            whenn()
            sut.uiState.chatConfirmation.onWriteAccessAnswered(false)

            then()
            verifySuspend(VerifyMode.exactly(1)) { fixture.deleteAccount() }
            assertFalse(sut.uiState.chatConfirmation.isWriteAccessRequested)
            val message = sut.uiState.notifications.presentationNotification
                .filterIsInstance<PresentationNotification.Message>()
                .single()
            assertEquals(
                SettingsRes.strings.settings_chat_confirmation_write_access_refused.desc(),
                message.message,
            )
            assertTrue(sut.uiState.deleteButton.isEnabled)
        }

    @Test
    fun `asks for write access only once`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteAccount() } throws ChatConfirmationError.WriteAccessRequired()
        val sut = fixture.agreedSut()
        sut.uiState.deleteButton.onClick?.invoke()

        whenn()
        sut.uiState.chatConfirmation.onWriteAccessAnswered(true)

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.deleteAccount() }
        assertFalse(sut.uiState.chatConfirmation.isWriteAccessRequested)
        sut.uiState.notifications.presentationNotification
            .filterIsInstance<PresentationNotification.Message>()
            .single()
    }

    private companion object {
        val DELETES_AT = Instant.parse("2026-12-03T14:00:00Z")
    }
}
