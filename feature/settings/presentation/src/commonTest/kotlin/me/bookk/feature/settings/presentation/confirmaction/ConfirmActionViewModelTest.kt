package me.bookk.feature.settings.presentation.confirmaction

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.domain.launch.ConfirmableAction
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.settings.domain.api.CompleteTelegramConfirmation
import me.bookk.feature.settings.presentation.FakeClientSettingsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
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
            return sut().apply { uiState.mainButton.onClick?.invoke() }
        }
    }

    @Test
    fun `shows the deletion warning with a destructive confirm`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut(ConfirmableAction.DELETE_ACCOUNT)

        then()
        val state = sut.uiState
        assertEquals(
            SettingsRes.strings.settings_confirm_delete_account_title.desc(),
            state.appBar.title,
        )
        assertEquals(TopBarSize.LARGE, state.appBar.size)
        assertEquals(SettingsRes.strings.settings_delete_account_confirmation.desc(), state.message)
        assertEquals(DesignSystem.images.material_delete, state.icon)
        assertEquals(ColorToken.Error, state.iconColor)
        assertTrue(state.isDestructive)
        assertEquals(
            SettingsRes.strings.settings_confirm_action_button.desc(),
            state.mainButton.text,
        )
    }

    @Test
    fun `shows what the personal data export confirms`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut(ConfirmableAction.EXPORT_PERSONAL_DATA)

        then()
        val state = sut.uiState
        assertEquals(SettingsRes.strings.settings_confirm_export_title.desc(), state.appBar.title)
        assertEquals(SettingsRes.strings.settings_confirm_export_message.desc(), state.message)
        assertEquals(DesignSystem.images.export_document, state.icon)
        assertEquals(ColorToken.ActionText, state.iconColor)
        assertFalse(state.isDestructive)
    }

    @Test
    fun `sends the signed launch data when confirm is tapped`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut(ConfirmableAction.EXPORT_PERSONAL_DATA)

        whenn()
        sut.uiState.mainButton.onClick?.invoke()

        then()
        verifySuspend {
            fixture.completeConfirmation(ConfirmableAction.EXPORT_PERSONAL_DATA, fixture.initData)
        }
        assertFalse(sut.uiState.mainButton.isLoading)
    }

    @Test
    fun `goes back without confirming`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf(ConfirmActionDestinations.Back),
            sut.uiState.navigation.navigationDestination,
        )
        verifySuspend(VerifyMode.not) { fixture.completeConfirmation(any(), any()) }
    }

    @Test
    fun `announces the deleted account`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut(ConfirmableAction.DELETE_ACCOUNT)

        whenn()
        sut.uiState.mainButton.onClick?.invoke()

        then()
        val message = sut.uiState.notifications.presentationNotification
            .filterIsInstance<PresentationNotification.GlobalMessage>()
            .single()
        assertEquals(
            SettingsRes.strings.settings_delete_account_success_message.desc(),
            message.text,
        )
    }

    @Test
    fun `plays success haptic when the account is deleted`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut(ConfirmableAction.DELETE_ACCOUNT)

        whenn()
        sut.uiState.mainButton.onClick?.invoke()

        then()
        assertEquals(
            1,
            sut.uiState.notifications.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `opens the export being prepared once it starts`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut(ConfirmableAction.EXPORT_PERSONAL_DATA)

        whenn()
        sut.uiState.mainButton.onClick?.invoke()

        then()
        assertEquals(
            listOf(ConfirmActionDestinations.PersonalDataExport),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `plays success haptic when the export starts`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut(ConfirmableAction.EXPORT_PERSONAL_DATA)

        whenn()
        sut.uiState.mainButton.onClick?.invoke()

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
            sut.uiState.error,
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
            sut.uiState.error,
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
            sut.uiState.error,
        )
    }

    @Test
    fun `explains an export already made today`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.failingSut(CompleteTelegramConfirmation.Error.ExportTooFrequent())

        then()
        assertEquals(SettingsRes.strings.settings_export_too_frequent.desc(), sut.uiState.error)
    }

    @Test
    fun `offers the way back to settings after a confirmation error`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.failingSut(CompleteTelegramConfirmation.Error.TelegramAccountMismatch())

        then()
        assertEquals(
            SettingsRes.strings.settings_confirm_action_open_settings.desc(),
            sut.uiState.mainButton.text,
        )
        assertTrue(sut.uiState.mainButton.isEnabled)
        assertFalse(sut.uiState.isDestructive)
    }

    @Test
    fun `opens settings instead of confirming again after a confirmation error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.failingSut(CompleteTelegramConfirmation.Error.ConfirmationExpired())

        whenn()
        sut.uiState.mainButton.onClick?.invoke()

        then()
        assertEquals(
            listOf(ConfirmActionDestinations.Settings),
            sut.uiState.navigation.navigationDestination,
        )
        verifySuspend(VerifyMode.exactly(1)) { fixture.completeConfirmation(any(), any()) }
    }

    @Test
    fun `lets the person confirm again after an unexpected failure`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.failingSut(TestException())

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertNull(sut.uiState.error)
        assertEquals(
            SettingsRes.strings.settings_confirm_action_button.desc(),
            sut.uiState.mainButton.text,
        )
        assertTrue(sut.uiState.mainButton.isEnabled)
    }
}
