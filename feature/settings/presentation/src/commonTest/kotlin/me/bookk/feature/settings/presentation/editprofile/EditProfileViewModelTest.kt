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
import me.bookk.designsystem.uistate.InputType
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
        val getSettings = mock<GetSettings>()
        val editProfile = mock<EditProfile>()
        val errorMapper = FakeErrorMapper()

        init {
            stubProfile()
        }

        fun sut(): EditProfileViewModel {
            return EditProfileViewModel(
                getSettings = getSettings,
                editProfile = editProfile,
                validateName = FakeValidateName(),
                settingsStateFactory = FakeSettingsStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }

        fun presentedSut(): EditProfileViewModel {
            return sut().also { it.onViewPresented() }
        }

        fun stubProfile(
            firstName: String = "Anna",
            lastName: String = "Smith",
            email: String = "anna@example.com",
            phone: String = "+48123456789",
        ) {
            everySuspend { getSettings() } returns
                Settings(
                    ColorScheme.SYSTEM,
                    SettingsProfile(firstName, lastName, email, phone, isEmailDeliverable = true),
                )
        }

        fun stubSaveSucceeds() {
            everySuspend { editProfile(any(), any(), any()) } returns Unit
        }
    }

    private fun EditProfileViewModel.typeName(text: String) {
        (uiState.name as FakeTextFieldState).type(text)
    }

    private fun EditProfileViewModel.typePhone(text: String) {
        (uiState.phone as FakeTextFieldState).type(text)
    }

    private fun EditProfileViewModel.save() {
        uiState.confirmButton.onClick?.invoke()
    }

    @Test
    fun `labels the title, the name field and save`() = runUnitTest {
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
            SettingsRes.strings.settings_edit_profile_full_name.desc(),
            sut.uiState.name.label,
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
    fun `shows first and last name in one field with save disabled`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.presentedSut()

        then()
        assertEquals("Anna Smith", sut.uiState.name.text)
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `shows only the first name when the last name is empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubProfile(lastName = "")

        whenn()
        val sut = fixture.presentedSut()

        then()
        assertEquals("Anna", sut.uiState.name.text)
    }

    @Test
    fun `ignores typing before the profile is loaded`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onNameTextChanged("Maria")

        then()
        assertEquals("", sut.uiState.name.text)
    }

    @Test
    fun `saves the first word as first name and the rest as last name`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveSucceeds()
        val sut = fixture.presentedSut()
        sut.typeName("Maria Anna Brown")

        whenn()
        sut.save()

        then()
        verifySuspend { fixture.editProfile("Maria", "Anna Brown", "+48123456789") }
    }

    @Test
    fun `saves an empty last name when only one name is typed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveSucceeds()
        val sut = fixture.presentedSut()
        sut.typeName("Maria")

        whenn()
        sut.save()

        then()
        verifySuspend { fixture.editProfile("Maria", "", "+48123456789") }
    }

    @Test
    fun `ignores extra spaces around and between names when saving`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveSucceeds()
        val sut = fixture.presentedSut()
        sut.typeName("  Maria   Brown ")

        whenn()
        sut.save()

        then()
        verifySuspend { fixture.editProfile("Maria", "Brown", "+48123456789") }
    }

    @Test
    fun `enables save when the name changes`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()

        whenn()
        sut.typeName("Maria Brown")

        then()
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `does not show an error while typing`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()

        whenn()
        sut.typeName("M")

        then()
        assertEquals(ValidationState.DEFAULT, sut.uiState.name.validationState)
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `shows an error on save when the first name is too short`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()
        sut.typeName("M Brown")

        whenn()
        sut.save()

        then()
        assertEquals(
            SettingsRes.strings.settings_edit_profile_name_error.desc(),
            sut.uiState.name.supportingTextRes,
        )
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `shows an error on save when the name is blank`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()
        sut.typeName("   ")

        whenn()
        sut.save()

        then()
        assertEquals(
            SettingsRes.strings.settings_edit_profile_name_error.desc(),
            sut.uiState.name.supportingTextRes,
        )
    }

    @Test
    fun `does not save an invalid name`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.presentedSut()
        sut.typeName("M")

        whenn()
        sut.save()

        then()
        verifySuspend(VerifyMode.not) { fixture.editProfile(any(), any(), any()) }
    }

    @Test
    fun `clears the name error once the name is edited`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()
        sut.typeName("M")
        sut.save()

        whenn()
        sut.typeName("Maria")

        then()
        assertNull(sut.uiState.name.supportingTextRes)
        assertTrue(sut.uiState.name.isValid)
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `shows the changed email when presented again and keeps the unsaved name`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.presentedSut()
        sut.typeName("Maria Brown")
        fixture.stubProfile(email = "anna@new.example.com")

        whenn()
        sut.onViewPresented()

        then()
        assertEquals("anna@new.example.com", sut.uiState.email.text)
        assertEquals("Maria Brown", sut.uiState.name.text)
        assertTrue(sut.uiState.confirmButton.isEnabled)
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
    fun `goes back with save disabled after the name is saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveSucceeds()
        val sut = fixture.presentedSut()
        sut.typeName("Maria Brown")

        whenn()
        sut.save()

        then()
        assertEquals(
            listOf<EditProfileNavigationDestination>(EditProfileNavigationDestination.Back),
            sut.uiState.navigation.navigationDestination,
        )
        assertFalse(sut.uiState.confirmButton.isEnabled)
        assertFalse(sut.uiState.confirmButton.isLoading)
    }

    @Test
    fun `plays success haptic when the name is saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveSucceeds()
        val sut = fixture.presentedSut()
        sut.typeName("Maria Brown")

        whenn()
        sut.save()

        then()
        assertEquals(
            1,
            sut.uiState.notification.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `does not play success haptic when saving the name fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any(), any()) } throws TestException()
        val sut = fixture.presentedSut()
        sut.typeName("Maria Brown")

        whenn()
        sut.save()

        then()
        assertTrue(
            sut.uiState.notification.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `keeps save enabled after saving fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any(), any()) } throws TestException()
        val sut = fixture.presentedSut()
        sut.typeName("Maria Brown")

        whenn()
        sut.save()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertTrue(sut.uiState.confirmButton.isEnabled)
        assertFalse(sut.uiState.confirmButton.isLoading)
    }

    @Test
    fun `labels the phone field as an optional phone input`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            SettingsRes.strings.settings_edit_profile_phone.desc(),
            sut.uiState.phone.label,
        )
        assertEquals(InputType.PHONE, sut.uiState.phone.inputType)
        assertEquals(
            SettingsRes.strings.settings_edit_profile_phone_hint.desc(),
            sut.uiState.phone.supportingTextRes,
        )
    }

    @Test
    fun `shows the stored phone`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.presentedSut()

        then()
        assertEquals("+48123456789", sut.uiState.phone.text)
    }

    @Test
    fun `shows an empty phone when the profile has none`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubProfile(phone = "")

        whenn()
        val sut = fixture.presentedSut()

        then()
        assertEquals("", sut.uiState.phone.text)
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `ignores phone typing before the profile is loaded`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onPhoneTextChanged("+1")

        then()
        assertEquals("", sut.uiState.phone.text)
    }

    @Test
    fun `keeps only digits and a leading plus in the phone`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()

        whenn()
        sut.typePhone("+48 (123) 456-789")

        then()
        assertEquals("+48123456789", sut.uiState.phone.text)
    }

    @Test
    fun `enables save when only the phone changes`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()

        whenn()
        sut.typePhone("+48999")

        then()
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `disables save when the phone is changed back`() = runUnitTest {
        given()
        val sut = Fixture().presentedSut()
        sut.typePhone("+48999")

        whenn()
        sut.typePhone("+48123456789")

        then()
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `saves the typed phone with the name`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveSucceeds()
        val sut = fixture.presentedSut()
        sut.typePhone("+48999")

        whenn()
        sut.save()

        then()
        verifySuspend { fixture.editProfile("Anna", "Smith", "+48999") }
    }

    @Test
    fun `saves an empty phone to remove it`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveSucceeds()
        val sut = fixture.presentedSut()
        sut.typePhone("")

        whenn()
        sut.save()

        then()
        verifySuspend { fixture.editProfile("Anna", "Smith", "") }
    }

    @Test
    fun `plays success haptic when the phone is saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveSucceeds()
        val sut = fixture.presentedSut()
        sut.typePhone("+48999")

        whenn()
        sut.save()

        then()
        assertEquals(
            1,
            sut.uiState.notification.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `shows phone taken error on the phone field`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any(), any()) } throws
            EditProfile.Error.ContactAlreadyTaken()
        val sut = fixture.presentedSut()
        sut.typePhone("+48999")

        whenn()
        sut.save()

        then()
        assertEquals(
            SettingsRes.strings.settings_edit_profile_phone_taken_error.desc(),
            sut.uiState.phone.supportingTextRes,
        )
        assertEquals(ValidationState.ERROR, sut.uiState.phone.validationState)
        assertFalse(sut.uiState.confirmButton.isEnabled)
        assertTrue(sut.uiState.notification.presentationNotification.isEmpty())
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `restores the phone hint once the taken phone is edited`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any(), any()) } throws
            EditProfile.Error.ContactAlreadyTaken()
        val sut = fixture.presentedSut()
        sut.typePhone("+48999")
        sut.save()

        whenn()
        sut.typePhone("+48998")

        then()
        assertEquals(
            SettingsRes.strings.settings_edit_profile_phone_hint.desc(),
            sut.uiState.phone.supportingTextRes,
        )
        assertEquals(ValidationState.DEFAULT, sut.uiState.phone.validationState)
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `shows email verification required error as an error message`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editProfile(any(), any(), any()) } throws
            EditProfile.Error.EmailChangeRequiresVerification()
        val sut = fixture.presentedSut()
        sut.typeName("Maria Brown")

        whenn()
        sut.save()

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
}
