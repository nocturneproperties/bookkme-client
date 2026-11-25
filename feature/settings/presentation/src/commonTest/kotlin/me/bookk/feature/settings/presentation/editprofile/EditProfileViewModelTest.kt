package me.bookk.feature.settings.presentation.editprofile

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
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.error.PresentationNotification.GlobalMessage
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.settings.domain.api.EditProfile
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.api.entity.Settings
import me.bookk.feature.settings.domain.api.entity.SettingsProfile
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
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
            everySuspend { invoke() } returns
                Settings(ColorScheme.SYSTEM, SettingsProfile("Anna", "Smith", "anna@example.com"))
        }
        val editProfile = mock<EditProfile>()
        val errorMapper = FakeErrorMapper()

        fun sut() = EditProfileViewModel(
            getSettings = getSettings,
            editProfile = editProfile,
            validateName = FakeValidateName(),
            settingsStateFactory = FakeSettingsStateFactory(),
            vmArgs = VmArgs(errorMapper),
        )

        fun presentedSut(): EditProfileViewModel {
            return sut().also { it.onViewPresented() }
        }

        fun stubProfile(email: String) {
            everySuspend { getSettings() } returns
                Settings(ColorScheme.SYSTEM, SettingsProfile("Anna", "Smith", email))
        }
    }

    @Test
    fun `labels the profile fields and actions`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            SettingsRes.strings.settings_edit_profile_title.desc(),
            sut.uiState.appBar.title,
        )
        assertEquals(
            SettingsRes.strings.settings_edit_profile_first_name.desc(),
            sut.uiState.name.label,
        )
        assertEquals(
            SettingsRes.strings.settings_edit_profile_last_name.desc(),
            sut.uiState.lastName.label,
        )
        assertEquals(DesignSystem.strings.action_save.desc(), sut.uiState.confirmButton.text)
    }

    @Test
    fun `shows the email read only with how a new one is confirmed`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.presentedSut()

        then()
        assertEquals(SettingsRes.strings.settings_profile_email.desc(), sut.uiState.email.label)
        assertEquals("anna@example.com", sut.uiState.email.text)
        assertTrue(sut.uiState.email.readOnly)
        assertEquals(
            SettingsRes.strings.settings_edit_profile_email_hint.desc(),
            sut.uiState.email.supportingTextRes,
        )
        assertEquals(
            SettingsRes.strings.settings_edit_profile_change_email.desc(),
            sut.uiState.changeEmail.text,
        )
    }

    @Test
    fun `shows the changed email when presented again and keeps unsaved names`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.presentedSut()
        sut.onFirstNameTextChanged("Maria")
        fixture.stubProfile("anna@new.example.com")

        whenn()
        sut.onViewPresented()

        then()
        assertEquals("anna@new.example.com", sut.uiState.email.text)
        assertEquals("Maria", sut.uiState.name.text)
    }

    @Test
    fun `opens the email change when change is tapped`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()

        whenn()
        sut.uiState.changeEmail.onClick?.invoke()

        then()
        assertEquals(
            listOf<EditProfileNavigationDestination>(EditProfileNavigationDestination.ChangeEmail),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `goes back when back is tapped`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf<EditProfileNavigationDestination>(EditProfileNavigationDestination.Back),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `saves the names typed into the fields when save is tapped`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any()) } returns Unit
        val sut = fixture.presentedSut()
        (sut.uiState.name as FakeTextFieldState).type("Maria")
        (sut.uiState.lastName as FakeTextFieldState).type("Brown")

        whenn()
        sut.uiState.confirmButton.onClick?.invoke()

        then()
        verifySuspend { fixture.editProfile("Maria", "Brown") }
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
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `keeps unsaved name changes when presented again`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()
        sut.onFirstNameTextChanged("Maria")

        whenn()
        sut.onViewPresented()

        then()
        assertEquals("Maria", sut.uiState.name.text)
        assertTrue(sut.uiState.confirmButton.isEnabled)
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

        then()
        assertEquals(ValidationState.DEFAULT, sut.uiState.lastName.validationState)
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `shows errors for every invalid name on save`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()
        sut.onFirstNameTextChanged("A")
        sut.onLastNameTextChanged("S")

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertEquals(
            SettingsRes.strings.settings_edit_profile_first_name_error.desc(),
            sut.uiState.name.supportingTextRes,
        )
        assertEquals(
            SettingsRes.strings.settings_edit_profile_last_name_error.desc(),
            sut.uiState.lastName.supportingTextRes,
        )
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `does not save an invalid profile`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.presentedSut()
        sut.onFirstNameTextChanged("A")

        whenn()
        sut.onConfirmButtonClick()

        then()
        verifySuspend(VerifyMode.not) { fixture.editProfile(any(), any()) }
    }

    @Test
    fun `clears field error once the field is edited`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()
        sut.onFirstNameTextChanged("A")
        sut.onConfirmButtonClick()

        whenn()
        sut.onFirstNameTextChanged("Maria")

        then()
        assertNull(sut.uiState.name.supportingTextRes)
        assertTrue(sut.uiState.name.isValid)
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `saves edited profile and goes back`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any()) } returns Unit
        val sut = fixture.presentedSut()
        sut.onFirstNameTextChanged("Maria")

        whenn()
        sut.onConfirmButtonClick()

        then()
        verifySuspend { fixture.editProfile("Maria", "Smith") }
        assertEquals(
            listOf<EditProfileNavigationDestination>(EditProfileNavigationDestination.Back),
            sut.uiState.navigation.navigationDestination,
        )
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `plays success haptic when profile is saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any()) } returns Unit
        val sut = fixture.presentedSut()
        sut.onFirstNameTextChanged("Maria")

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertEquals(
            1,
            sut.uiState.notification.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `does not play success haptic when profile save fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any()) } throws TestException()
        val sut = fixture.presentedSut()
        sut.onFirstNameTextChanged("Maria")

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertTrue(
            sut.uiState.notification.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `keeps save enabled after save fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any()) } throws TestException()
        val sut = fixture.presentedSut()
        sut.onFirstNameTextChanged("Maria")

        whenn()
        sut.onConfirmButtonClick()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertTrue(sut.uiState.confirmButton.isEnabled)
        assertFalse(sut.uiState.confirmButton.isLoading)
    }

    @Test
    fun `shows email taken error as an error message`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any()) } throws
            EditProfile.Error.ContactAlreadyTaken()
        val sut = fixture.presentedSut()
        sut.onFirstNameTextChanged("Maria")

        whenn()
        sut.onConfirmButtonClick()

        then()
        val message = sut.uiState.notification.assertSingle<GlobalMessage>()
        assertEquals(
            SettingsRes.strings.settings_edit_profile_email_taken_error.desc(),
            message.text,
        )
        assertEquals(GlobalMessage.State.ERROR, message.state)
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `shows email verification required error as an error message`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any()) } throws
            EditProfile.Error.EmailChangeRequiresVerification()
        val sut = fixture.presentedSut()
        sut.onFirstNameTextChanged("Maria")

        whenn()
        sut.onConfirmButtonClick()

        then()
        val message = sut.uiState.notification.assertSingle<GlobalMessage>()
        assertEquals(
            SettingsRes.strings.settings_edit_profile_email_verification_required_error.desc(),
            message.text,
        )
        assertEquals(GlobalMessage.State.ERROR, message.state)
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `keeps save enabled after an email error`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any()) } throws
            EditProfile.Error.EmailChangeRequiresVerification()
        val sut = fixture.presentedSut()
        sut.onFirstNameTextChanged("Maria")

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertTrue(sut.uiState.confirmButton.isEnabled)
        assertFalse(sut.uiState.confirmButton.isLoading)
    }
}
