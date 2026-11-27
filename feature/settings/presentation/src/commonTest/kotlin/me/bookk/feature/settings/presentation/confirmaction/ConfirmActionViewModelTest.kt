package me.bookk.feature.settings.presentation.confirmaction

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.domain.launch.ConfirmableAction
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
import me.bookk.feature.settings.domain.api.CompleteTelegramConfirmation
import me.bookk.feature.settings.presentation.FakeClientSettingsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConfirmActionViewModelTest {
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
        val initData = "auth_date=1&start_param=op_delete-account_00&hash=00"
        val completeConfirmation = mock<CompleteTelegramConfirmation> {
            everySuspend { invoke(any(), any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut(
            action: ConfirmableAction = ConfirmableAction.DELETE_ACCOUNT,
        ): ConfirmActionViewModel {
            return ConfirmActionViewModel(
                action = action,
                initData = initData,
                completeConfirmation = completeConfirmation,
                stateFactory = FakeClientSettingsStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }

        fun failingSut(error: Throwable): ConfirmActionViewModel {
            everySuspend { completeConfirmation(any(), any()) } throws error
            return sut()
        }
    }

    @Test
    fun `sends the signed launch data as soon as the screen opens`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(ConfirmableAction.EXPORT_PERSONAL_DATA)

        then()
        verifySuspend {
            fixture.completeConfirmation(ConfirmableAction.EXPORT_PERSONAL_DATA, fixture.initData)
        }
    }

    @Test
    fun `plays success haptic once the action is confirmed`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut(ConfirmableAction.DELETE_ACCOUNT)

        then()
        assertEquals(
            listOf(PresentationNotification.SuccessHaptic),
            sut.uiState.notifications.presentationNotification,
        )
    }

    @Test
    fun `does not play success haptic when the confirmation fails`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.failingSut(CompleteTelegramConfirmation.Error.ConfirmationExpired())

        then()
        assertTrue(
            sut.uiState.notifications.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `explains an expired or used confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.failingSut(CompleteTelegramConfirmation.Error.ConfirmationExpired())

        then()
        assertEquals(
            SettingsRes.strings.settings_confirm_action_expired.desc(),
            sut.singleMessage(),
        )
    }

    @Test
    fun `explains a confirmation requested from another telegram account`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.failingSut(CompleteTelegramConfirmation.Error.TelegramAccountMismatch())

        then()
        assertEquals(
            SettingsRes.strings.settings_confirm_action_other_account.desc(),
            sut.singleMessage(),
        )
    }

    @Test
    fun `asks to reopen the mini app when the telegram data is invalid`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.failingSut(CompleteTelegramConfirmation.Error.TelegramDataInvalid())

        then()
        assertEquals(
            SettingsRes.strings.settings_confirm_action_reopen.desc(),
            sut.singleMessage(),
        )
    }

    @Test
    fun `explains an export already made today`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.failingSut(CompleteTelegramConfirmation.Error.ExportTooFrequent())

        then()
        assertEquals(SettingsRes.strings.settings_export_too_frequent.desc(), sut.singleMessage())
    }

    @Test
    fun `explains that an account on hold can't be deleted`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.failingSut(CompleteTelegramConfirmation.Error.DeletionOnHold())

        then()
        assertEquals(
            SettingsRes.strings.settings_delete_account_on_hold.desc(),
            sut.singleMessage(),
        )
    }

    @Test
    fun `shows the mapped error for an unexpected failure`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.failingSut(TestException())

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    private fun ConfirmActionViewModel.singleMessage() =
        uiState.notifications.presentationNotification
            .filterIsInstance<PresentationNotification.Message>()
            .single()
            .message
}
