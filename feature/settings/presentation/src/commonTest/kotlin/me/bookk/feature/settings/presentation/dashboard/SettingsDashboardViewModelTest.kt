package me.bookk.feature.settings.presentation.dashboard

import dev.icerock.moko.resources.desc.desc
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
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.tap
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.UpdateColorScheme
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.api.entity.Settings
import me.bookk.feature.settings.domain.api.entity.SettingsProfile
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import me.bookk.feature.settings.presentation.dashboard.AppearanceSection.UIColorScheme
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
            everySuspend { invoke() } returns Settings(ColorScheme.DARK, SettingsProfile("Anna", "Smith", "anna@example.com"))
        }
        val updateColorScheme = mock<UpdateColorScheme>()
        val deviceFacade = mock<DeviceFacade> {
            every { openUrlPreview(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = SettingsDashboardViewModel(
            getSettings = getSettings,
            updateColorScheme = updateColorScheme,
            deviceFacade = deviceFacade,
            settingsStateFactory = FakeSettingsStateFactory(),
            vmArgs = VmArgs(errorMapper)
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
        assertEquals(listOf<PresentationNotification>(PresentationNotification.Unauthorized), sut.uiState.notification.presentationNotification)
    }

    @Test
    fun `opens terms and policy links`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.showTerms()
        sut.showPolicy()

        then()
        verify(dev.mokkery.verify.VerifyMode.exactly(2)) { fixture.deviceFacade.openUrlPreview(any()) }
    }

    @Test
    fun `navigates to edit profile from app bar action`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.actions.items.single().onClick()

        then()
        assertEquals(listOf<SettingsDashboardDestination>(SettingsDashboardDestination.EditProfile), sut.uiState.navigation.navigationDestination)
    }
}
