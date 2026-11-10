package me.bookk.feature.settings.presentation.dashboard

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import library.device.api.DeviceFacade
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
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
import me.bookk.designsystem.test.tap
import me.bookk.feature.settings.domain.api.GetLegalDocumentStatuses
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.UpdateColorScheme
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.domain.api.entity.Settings
import me.bookk.feature.settings.domain.api.entity.SettingsProfile
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import me.bookk.feature.settings.presentation.dashboard.AppearanceSection.UIColorScheme
import me.bookk.feature.settings.presentation.privacyPolicyStatus
import me.bookk.feature.settings.presentation.termsStatus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SettingsDashboardViewModelTest {

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
        val getSettings = mock<GetSettings> {
            everySuspend { invoke() } returns
                Settings(ColorScheme.DARK, SettingsProfile("Anna", "Smith", "anna@example.com"))
        }
        val updateColorScheme = mock<UpdateColorScheme>()
        val statuses = MutableStateFlow<List<LegalDocumentStatus>>(emptyList())
        val getLegalDocumentStatuses = mock<GetLegalDocumentStatuses> {
            every { flow() } returns statuses
            everySuspend { refresh() } returns emptyList()
        }
        val device = mock<DeviceFacade> {
            every { mail(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut(): SettingsDashboardViewModel {
            return SettingsDashboardViewModel(
                getSettings = getSettings,
                getLegalDocumentStatuses = getLegalDocumentStatuses,
                updateColorScheme = updateColorScheme,
                device = device,
                settingsStateFactory = FakeSettingsStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }
    }

    @Test
    fun `sets the personal data export account label`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            SettingsRes.strings.settings_export_title.desc(),
            sut.uiState.account.dataExport.text,
        )
    }

    @Test
    fun `renders profile and color scheme when presented`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onViewPresented()

        then()
        assertEquals(UIColorScheme.DARK, sut.uiState.appearance.colorScheme)
        assertEquals("Anna".desc(), sut.uiState.profile.name)
        assertEquals("Smith".desc(), sut.uiState.profile.lastName)
        assertEquals("anna@example.com".desc(), sut.uiState.profile.email)
    }

    @Test
    fun `shows mapped error when settings cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSettings() } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `saves selected color scheme`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateColorScheme.invoke(any()) } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.onSchemeSelected(UIColorScheme.LIGHT)

        then()
        verifySuspend { fixture.updateColorScheme.invoke(ColorScheme.LIGHT) }
        assertEquals(UIColorScheme.LIGHT, sut.uiState.appearance.colorScheme)
    }

    @Test
    fun `plays success haptic when color scheme is saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateColorScheme.invoke(any()) } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.onSchemeSelected(UIColorScheme.LIGHT)

        then()
        assertEquals(
            1,
            sut.uiState.notification.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `does not play success haptic when color scheme save fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateColorScheme.invoke(any()) } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.onSchemeSelected(UIColorScheme.LIGHT)

        then()
        assertTrue(
            sut.uiState.notification.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `keeps previous scheme when saving fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateColorScheme.invoke(any()) } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.onSchemeSelected(UIColorScheme.LIGHT)

        then()
        assertEquals(UIColorScheme.SYSTEM, sut.uiState.appearance.colorScheme)
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `asks for confirmation before logging out`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onLogOutClick()

        then()
        sut.uiState.notification.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `posts unauthorized after logout is confirmed`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.onLogOutClick()
        val dialog = sut.uiState.notification.assertSingle<PresentationNotification.Message>()
        sut.uiState.notification.removeFirst()

        whenn()
        dialog.tap(ActionType.NEGATIVE)

        then()
        assertEquals(
            listOf<PresentationNotification>(PresentationNotification.Unauthorized),
            sut.uiState.notification.presentationNotification,
        )
    }

    @Test
    fun `navigates to edit profile from app bar action`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.actions.items.single().onClick()

        then()
        assertEquals(
            listOf<SettingsDashboardDestination>(SettingsDashboardDestination.EditProfile),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `opens mail app addressed to support when contact is clicked`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.support.onContactClick()

        then()
        verify { fixture.device.mail("support@bookkme.com") }
    }

    @Test
    fun `labels the privacy account row`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            SettingsRes.strings.settings_privacy_title.desc(),
            sut.uiState.account.privacy.text,
        )
    }

    @Test
    fun `marks the privacy row when a legal document awaits acceptance`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.statuses.value = listOf(privacyPolicyStatus(), termsStatus())

        then()
        assertEquals(
            SettingsRes.strings.settings_privacy_action_required.desc(),
            sut.uiState.account.privacyStatus,
        )
    }

    @Test
    fun `clears the privacy row status once every document is accepted`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.statuses.value = listOf(privacyPolicyStatus(), termsStatus())
        val sut = fixture.sut()

        whenn()
        fixture.statuses.value = listOf(privacyPolicyStatus(accepted = true), termsStatus())

        then()
        assertNull(sut.uiState.account.privacyStatus)
    }

    @Test
    fun `refreshes legal document statuses when presented`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.getLegalDocumentStatuses.refresh() }
    }

    @Test
    fun `keeps the cached privacy status without a notification when refreshing fails`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.statuses.value = listOf(privacyPolicyStatus(), termsStatus())
            everySuspend { fixture.getLegalDocumentStatuses.refresh() } throws TestException()
            val sut = fixture.sut()

            whenn()
            sut.onViewPresented()

            then()
            assertEquals(
                SettingsRes.strings.settings_privacy_action_required.desc(),
                sut.uiState.account.privacyStatus,
            )
            assertTrue(sut.uiState.notification.presentationNotification.isEmpty())
        }
}
