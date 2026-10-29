package me.bookk.feature.settings.presentation.editprofile

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.settings.domain.api.EditProfile
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.api.entity.Settings
import me.bookk.feature.settings.domain.api.entity.SettingsProfile
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import me.bookk.feature.settings.presentation.FakeValidateEmail
import me.bookk.feature.settings.presentation.FakeValidateName
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EditProfileViewModelTest {

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
            everySuspend { invoke() } returns Settings(ColorScheme.SYSTEM, SettingsProfile("Anna", "Smith", "anna@example.com"))
        }
        val editProfile = mock<EditProfile>()
        val errorMapper = FakeErrorMapper()

        fun sut() = EditProfileViewModel(
            getSettings = getSettings,
            editProfile = editProfile,
            validateName = FakeValidateName(),
            validateEmail = FakeValidateEmail(),
            settingsStateFactory = FakeSettingsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )

        fun presentedSut(): EditProfileViewModel {
            return sut().also { it.onViewPresented() }
        }
    }

    @Test
    fun `renders current profile with save disabled`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.presentedSut()

        then()
        assertEquals("Anna", sut.uiState.name.text)
        assertEquals("Smith", sut.uiState.lastName.text)
        assertEquals("anna@example.com", sut.uiState.email.text)
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `ignores typing before profile is loaded`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onFirstNameTextChanged("Maria")

        then()
        assertEquals("", sut.uiState.name.text)
    }

    @Test
    fun `enables save when a field changes`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()

        whenn()
        sut.onFirstNameTextChanged("Maria")

        then()
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `does not show errors while typing`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()

        whenn()
        sut.onLastNameTextChanged("S")
        sut.onEmailTextChanged("broken")

        then()
        assertEquals(ValidationState.DEFAULT, sut.uiState.lastName.validationState)
        assertEquals(ValidationState.DEFAULT, sut.uiState.email.validationState)
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `shows errors for every invalid field on save`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()
        sut.onFirstNameTextChanged("A")
        sut.onLastNameTextChanged("S")
        sut.onEmailTextChanged("broken")

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertEquals(SettingsRes.strings.settings_edit_profile_first_name_error.desc(), sut.uiState.name.supportingTextRes)
        assertEquals(SettingsRes.strings.settings_edit_profile_last_name_error.desc(), sut.uiState.lastName.supportingTextRes)
        assertEquals(SettingsRes.strings.settings_edit_profile_email_error.desc(), sut.uiState.email.supportingTextRes)
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `does not save an invalid profile`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.presentedSut()
        sut.onEmailTextChanged("broken")

        whenn()
        sut.onConfirmButtonClick()

        then()
        verifySuspend(VerifyMode.not) { fixture.editProfile(any(), any(), any()) }
    }

    @Test
    fun `clears field error once the field is edited`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()
        sut.onEmailTextChanged("broken")
        sut.onConfirmButtonClick()

        whenn()
        sut.onEmailTextChanged("maria@example.com")

        then()
        assertNull(sut.uiState.email.supportingTextRes)
        assertTrue(sut.uiState.email.isValid)
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `saves edited profile and goes back`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any(), any()) } returns Unit
        val sut = fixture.presentedSut()
        sut.onFirstNameTextChanged("Maria")

        whenn()
        sut.onConfirmButtonClick()

        then()
        verifySuspend { fixture.editProfile("Maria", "Smith", "anna@example.com") }
        assertEquals(listOf<EditProfileNavigationDestination>(EditProfileNavigationDestination.Back), sut.uiState.navigation.navigationDestination)
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `keeps save enabled after save fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any(), any()) } throws TestException()
        val sut = fixture.presentedSut()
        sut.onFirstNameTextChanged("Maria")

        whenn()
        sut.onConfirmButtonClick()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertTrue(sut.uiState.confirmButton.isEnabled)
        assertFalse(sut.uiState.confirmButton.isLoading)
    }
}
