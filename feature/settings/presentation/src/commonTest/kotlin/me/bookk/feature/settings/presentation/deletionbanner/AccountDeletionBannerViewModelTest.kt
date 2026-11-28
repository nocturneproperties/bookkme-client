package me.bookk.feature.settings.presentation.deletionbanner

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.domain.entity.Error
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
import me.bookk.designsystem.test.assertSingle
import me.bookk.feature.settings.domain.api.CancelAccountDeletion
import me.bookk.feature.settings.domain.api.GetAccountDeletion
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

class AccountDeletionBannerViewModelTest {

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
        val deletion = MutableStateFlow<Instant?>(null)
        val getAccountDeletion = mock<GetAccountDeletion> {
            every { flow() } returns deletion
            everySuspend { refresh() } returns Unit
        }
        val cancelAccountDeletion = mock<CancelAccountDeletion> {
            everySuspend { invoke() } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = AccountDeletionBannerViewModel(
            getAccountDeletion = getAccountDeletion,
            cancelAccountDeletion = cancelAccountDeletion,
            dateLocalizer = FakeDateLocalizer(),
            settingsStateFactory = FakeSettingsStateFactory(),
            vmArgs = VmArgs(errorMapper),
        )

        fun pendingSut(): AccountDeletionBannerViewModel {
            deletion.value = DELETES_AT
            return sut()
        }
    }

    @Test
    fun `hides the banner while no deletion is pending`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.isVisible)
    }

    @Test
    fun `shows when the pending deletion runs`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.pendingSut()

        then()
        assertTrue(sut.uiState.isVisible)
        assertEquals(
            SettingsRes.strings.settings_account_deletion_banner.format(
                DELETES_AT.toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
            ),
            sut.uiState.message,
        )
    }

    @Test
    fun `offers to cancel the pending deletion`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.pendingSut()

        then()
        assertEquals(
            SettingsRes.strings.settings_account_deletion_banner_cancel.desc(),
            sut.uiState.cancelButton.text,
        )
        assertTrue(sut.uiState.cancelButton.isEnabled)
    }

    @Test
    fun `hides the banner once the deletion is no longer pending`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.pendingSut()

        whenn()
        fixture.deletion.value = null

        then()
        assertFalse(sut.uiState.isVisible)
    }

    @Test
    fun `checks for a pending deletion each time the app is resumed`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.onAppResumed()
        sut.onAppResumed()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.getAccountDeletion.refresh() }
    }

    @Test
    fun `keeps the known deletion quietly when the check fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAccountDeletion.refresh() } throws TestException()
        val sut = fixture.pendingSut()

        whenn()
        sut.onAppResumed()

        then()
        assertTrue(sut.uiState.isVisible)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `signs out when the check finds the session gone`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAccountDeletion.refresh() } throws
            Error.Unauthorized(TestException())
        val sut = fixture.pendingSut()

        whenn()
        sut.onAppResumed()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Unauthorized>()
    }

    @Test
    fun `cancels the pending deletion when cancel is tapped`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.pendingSut()

        whenn()
        sut.uiState.cancelButton.onClick?.invoke()

        then()
        verifySuspend { fixture.cancelAccountDeletion() }
    }

    @Test
    fun `tells the user the deletion was cancelled`() = runUnitTest {
        given()
        val sut = Fixture().pendingSut()

        whenn()
        sut.uiState.cancelButton.onClick?.invoke()

        then()
        val message = sut.uiState.notifications.presentationNotification
            .filterIsInstance<PresentationNotification.GlobalMessage>()
            .single()
        assertEquals(SettingsRes.strings.settings_account_deletion_cancelled.desc(), message.text)
        assertEquals(PresentationNotification.GlobalMessage.State.SUCCESS, message.state)
    }

    @Test
    fun `plays success haptic when the deletion is cancelled`() = runUnitTest {
        given()
        val sut = Fixture().pendingSut()

        whenn()
        sut.uiState.cancelButton.onClick?.invoke()

        then()
        assertEquals(
            1,
            sut.uiState.notifications.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `does not play success haptic when cancelling fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.cancelAccountDeletion() } throws TestException()
        val sut = fixture.pendingSut()

        whenn()
        sut.uiState.cancelButton.onClick?.invoke()

        then()
        assertTrue(
            sut.uiState.notifications.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `shows mapped error when cancelling fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.cancelAccountDeletion() } throws TestException()
        val sut = fixture.pendingSut()

        whenn()
        sut.uiState.cancelButton.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertTrue(sut.uiState.isVisible)
    }

    @Test
    fun `lets the user try cancelling again after it fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.cancelAccountDeletion() } throws TestException()
        val sut = fixture.pendingSut()

        whenn()
        sut.uiState.cancelButton.onClick?.invoke()

        then()
        assertFalse(sut.uiState.cancelButton.isLoading)
        assertTrue(sut.uiState.cancelButton.isEnabled)
    }

    private companion object {
        val DELETES_AT = Instant.parse("2026-12-03T14:30:00Z")
    }
}
