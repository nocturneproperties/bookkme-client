package me.bookk.feature.authorization.presentation.sign_up

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.setMain
import library.device.api.DeviceFacade
import library.validation.api.ValidateEmail
import library.validation.api.ValidateName
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.coroutine.DispatcherProvider
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
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.authorization.domain.api.FinishSignUp
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.api.GetSignUpLegalDocuments
import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument.Document
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocuments
import me.bookk.feature.authorization.presentation.FakeAuthStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SignUpViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class FakeValidateName : ValidateName {
        override fun invoke(name: String): ValidateName.Result {
            return if (name.length >= 2) {
                ValidateName.Result.Valid
            } else {
                ValidateName.Result.Invalid.Length
            }
        }
    }

    private class FakeValidateEmail : ValidateEmail {
        override fun invoke(email: String): ValidateEmail.Result {
            return if ("@" in email) {
                ValidateEmail.Result.Valid
            } else {
                ValidateEmail.Result.Invalid.Format
            }
        }
    }

    private class Fixture {
        val startSignUp = mock<StartSignUp>()
        val pendingSignUp = StartSignUp.PendingSignUp(
            maskedEmail = "j***@example.com",
            expiresAt = Instant.fromEpochSeconds(1_000),
            resendAvailableAt = Instant.fromEpochSeconds(100),
        )
        val documents = SignUpLegalDocuments(
            privacyPolicy = SignUpLegalDocument(
                Document.PRIVACY_POLICY,
                "privacy-v3",
                "https://example.com/privacy",
            ),
            termsOfService = SignUpLegalDocument(
                Document.TERMS_OF_SERVICE,
                "terms-v2",
                "https://example.com/terms",
            ),
        )
        val getSignUpLegalDocuments = mock<GetSignUpLegalDocuments> {
            everySuspend { invoke() } returns documents
        }
        val deviceFacade = mock<DeviceFacade> {
            every { openUrlPreview(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()
        val userData = Capture.slot<StartSignUp.UserData>()

        fun sut(acceptLegalDocuments: Boolean = false): SignUpViewModel {
            return SignUpViewModel(
                validateName = FakeValidateName(),
                validateEmail = FakeValidateEmail(),
                startSignUp = startSignUp,
                getSignUpLegalDocuments = getSignUpLegalDocuments,
                deviceFacade = deviceFacade,
                stateFactory = FakeAuthStateFactory(),
                vmArgs = VmArgs(errorMapper),
            ).apply {
                if (acceptLegalDocuments) {
                    uiState.legalAcceptance.onCheckedChange?.invoke(true)
                }
            }
        }
    }

    private fun SignUpViewModel.fillValidForm() {
        onFirstNameTextChanged("Anna")
        onLastNameTextChanged("Smith")
        onEmailTextChanged("anna@example.com")
    }

    @Test
    fun `enables confirm after acceptance before the form is filled`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut(acceptLegalDocuments = true)

        then()
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `does not show errors while typing`() = runUnitTest {
        given()
        val sut = Fixture().sut(acceptLegalDocuments = true)

        whenn()
        sut.onFirstNameTextChanged("A")
        sut.onEmailTextChanged("anna")

        then()
        assertEquals(ValidationState.DEFAULT, sut.uiState.name.validationState)
        assertEquals(ValidationState.DEFAULT, sut.uiState.email.validationState)
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `shows errors for every invalid field on confirm`() = runUnitTest {
        given()
        val sut = Fixture().sut(acceptLegalDocuments = true)
        sut.onFirstNameTextChanged("A")
        sut.onLastNameTextChanged("S")
        sut.onEmailTextChanged("anna")

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertEquals(
            AuthRes.strings.sign_up_first_name_error.desc(),
            sut.uiState.name.supportingTextRes,
        )
        assertEquals(
            AuthRes.strings.sign_up_last_name_error.desc(),
            sut.uiState.lastName.supportingTextRes,
        )
        assertEquals(
            AuthRes.strings.sign_up_email_error.desc(),
            sut.uiState.email.supportingTextRes,
        )
        assertEquals(ValidationState.ERROR, sut.uiState.email.validationState)
        assertFalse(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `does not create account when the form is invalid`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut(acceptLegalDocuments = true)

        whenn()
        sut.onConfirmButtonClick()

        then()
        verifySuspend(VerifyMode.not) { fixture.startSignUp(any()) }
    }

    @Test
    fun `clears field error once the field is edited`() = runUnitTest {
        given()
        val sut = Fixture().sut(acceptLegalDocuments = true)
        sut.fillValidForm()
        sut.onFirstNameTextChanged("A")
        sut.onConfirmButtonClick()

        whenn()
        sut.onFirstNameTextChanged("An")

        then()
        assertEquals(ValidationState.DEFAULT, sut.uiState.name.validationState)
        assertNull(sut.uiState.name.supportingTextRes)
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `creates account with entered data`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startSignUp(capture(fixture.userData)) } returns fixture.pendingSignUp
        val sut = fixture.sut(acceptLegalDocuments = true)
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        val data = fixture.userData.get()
        assertEquals("Anna", data.firstName)
        assertEquals("Smith", data.lastName)
        assertEquals("anna@example.com", data.email)
        assertFalse(sut.uiState.confirmButton.isLoading)
    }

    @Test
    fun `plays success haptic when account is created`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startSignUp(any()) } returns fixture.pendingSignUp
        val sut = fixture.sut(acceptLegalDocuments = true)
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        sut.uiState.notification.assertSingle<PresentationNotification.SuccessHaptic>()
    }

    @Test
    fun `does not play success haptic when account creation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startSignUp(any()) } throws TestException()
        val sut = fixture.sut(acceptLegalDocuments = true)
        sut.fillValidForm()

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
    fun `marks email invalid when it already exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startSignUp(any()) } throws StartSignUp.Error.EmailAlreadyExist()
        val sut = fixture.sut(acceptLegalDocuments = true)
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertFalse(sut.uiState.email.isValid)
        assertEquals(ValidationState.ERROR, sut.uiState.email.validationState)
        assertTrue(sut.uiState.notification.presentationNotification.isEmpty())
    }

    @Test
    fun `marks email invalid when server rejects its format`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startSignUp(any()) } throws
            StartSignUp.Error.InvalidEmailFormat()
        val sut = fixture.sut(acceptLegalDocuments = true)
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertFalse(sut.uiState.email.isValid)
    }

    @Test
    fun `shows message when passkey verification fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startSignUp(any()) } throws
            FinishSignUp.Error.PasskeyVerificationFailed()
        val sut = fixture.sut(acceptLegalDocuments = true)
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        sut.uiState.notification.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows message when account creation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startSignUp(any()) } throws
            FinishSignUp.Error.AccountCreationFailed()
        val sut = fixture.sut(acceptLegalDocuments = true)
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        sut.uiState.notification.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `shows mapped error when sign up fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startSignUp(any()) } throws TestException()
        val sut = fixture.sut(acceptLegalDocuments = true)
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut(acceptLegalDocuments = true)

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf<SignUpNavigationDestination>(SignUpNavigationDestination.Back),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `explains expired signup verification`() = runUnitTest {
        listOf(
            FinishSignUp.Error.ChallengeWindowExpired() to
                AuthRes.strings.sign_up_challenge_expired_error,
        ).forEach { (error, expected) ->
            given()
            val fixture = Fixture()
            everySuspend { fixture.startSignUp(any()) } throws error
            val sut = fixture.sut(acceptLegalDocuments = true)
            sut.fillValidForm()

            whenn()
            sut.onConfirmButtonClick()

            then()
            assertEquals(
                listOf(expected.desc()),
                sut.uiState.notification.presentationNotification
                    .filterIsInstance<PresentationNotification.Message>()
                    .map { it.message },
            )
            assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
            assertTrue(
                sut.uiState.notification.presentationNotification.none {
                    it is PresentationNotification.SuccessHaptic
                },
            )
        }
    }

    @Test
    fun `requires explicit acceptance before submitting published documents`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertFalse(sut.uiState.confirmButton.isEnabled)
        assertFalse(sut.uiState.legalAcceptance.isChecked)
        assertEquals(ValidationState.ERROR, sut.uiState.legalAcceptance.validationState)
        assertFalse(sut.uiState.legalAcceptance.isValid)
        assertEquals(
            AuthRes.strings.sign_up_legal_acceptance_error.desc(),
            sut.uiState.legalAcceptance.supportingTextRes,
        )
        assertTrue(sut.uiState.legalAcceptance.isEnabled)
        assertFalse(sut.uiState.reloadLegalDocuments.isVisible)
        verifySuspend(VerifyMode.not) { fixture.startSignUp(any()) }
    }

    @Test
    fun `allows confirm before consent is validated`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.fillValidForm()

        then()
        assertTrue(sut.uiState.confirmButton.isEnabled)
        assertFalse(sut.uiState.legalAcceptance.isChecked)
        assertEquals(ValidationState.DEFAULT, sut.uiState.legalAcceptance.validationState)
    }

    @Test
    fun `validates profile fields and consent together`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertEquals(ValidationState.ERROR, sut.uiState.name.validationState)
        assertEquals(ValidationState.ERROR, sut.uiState.lastName.validationState)
        assertEquals(ValidationState.ERROR, sut.uiState.email.validationState)
        assertEquals(ValidationState.ERROR, sut.uiState.legalAcceptance.validationState)
        verifySuspend(VerifyMode.not) { fixture.startSignUp(any()) }
    }

    @Test
    fun `clears consent error when the checkbox changes`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.fillValidForm()
        sut.onConfirmButtonClick()

        whenn()
        sut.uiState.legalAcceptance.onCheckedChange?.invoke(true)

        then()
        assertEquals(ValidationState.DEFAULT, sut.uiState.legalAcceptance.validationState)
        assertNull(sut.uiState.legalAcceptance.supportingTextRes)
        assertTrue(sut.uiState.legalAcceptance.isValid)
        assertTrue(sut.uiState.legalAcceptance.isEnabled)
        assertTrue(sut.uiState.confirmButton.isEnabled)
    }

    @Test
    fun `uses one acceptance label containing both linked documents`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            AuthRes.strings.sign_up_accept_legal_documents.desc(),
            sut.uiState.legalAcceptance.text,
        )
        assertFalse(sut.uiState.legalAcceptance.isChecked)
    }

    @Test
    fun `one checkbox accepts both published versions`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents
        everySuspend { fixture.startSignUp(capture(fixture.userData)) } returns fixture.pendingSignUp
        val sut = fixture.sut()
        sut.fillValidForm()
        sut.uiState.legalAcceptance.onCheckedChange?.invoke(true)

        whenn()
        sut.uiState.confirmButton.onClick?.invoke()

        then()
        assertEquals(
            AcceptedLegalDocuments("privacy-v3", "terms-v2"),
            fixture.userData.get().acceptedDocuments,
        )
    }

    @Test
    fun `blocks signup when a required document is missing`() = runUnitTest {
        listOf(Document.PRIVACY_POLICY, Document.TERMS_OF_SERVICE).forEach { document ->
            given()
            val fixture = Fixture()
            everySuspend { fixture.getSignUpLegalDocuments() } throws
                GetSignUpLegalDocuments.Error.MissingDocument(document)
            val sut = fixture.sut()

            whenn()
            sut.fillValidForm()

            then()
            assertFalse(sut.uiState.confirmButton.isEnabled)
            assertNull(sut.uiState.legalDocuments)
            assertTrue(sut.uiState.legalAcceptance.isEnabled)
            assertEquals(ValidationState.ERROR, sut.uiState.legalAcceptance.validationState)
            assertEquals(
                AuthRes.strings.sign_up_legal_load_error.desc(),
                sut.uiState.legalAcceptance.supportingTextRes,
            )
            verifySuspend(VerifyMode.not) { fixture.startSignUp(any()) }
        }
    }

    @Test
    fun `revoking acceptance blocks signup`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents
        val sut = fixture.sut()
        sut.fillValidForm()
        sut.uiState.legalAcceptance.onCheckedChange?.invoke(true)

        whenn()
        sut.uiState.legalAcceptance.onCheckedChange?.invoke(false)
        sut.onConfirmButtonClick()

        then()
        assertFalse(sut.uiState.confirmButton.isEnabled)
        assertEquals(ValidationState.ERROR, sut.uiState.legalAcceptance.validationState)
        verifySuspend(VerifyMode.not) { fixture.startSignUp(any()) }
    }

    @Test
    fun `provides each document name and published URL for its inline link`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents

        whenn()
        val sut = fixture.sut()

        then()
        val documents = assertNotNull(sut.uiState.legalDocuments)
        assertEquals(
            AuthRes.strings.sign_up_privacy_policy_link.desc(),
            documents.privacyPolicy.linkText,
        )
        assertEquals(
            AuthRes.strings.sign_up_terms_of_service_link.desc(),
            documents.termsOfService.linkText,
        )
        assertEquals(fixture.documents.privacyPolicy.url, documents.privacyPolicy.linkUrl)
        assertEquals(fixture.documents.termsOfService.url, documents.termsOfService.linkUrl)
    }

    @Test
    fun `opens the published document link without accepting it`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents
        val sut = fixture.sut()

        whenn()
        sut.uiState.legalDocuments?.privacyPolicy?.onLinkClick?.invoke()
        sut.uiState.legalDocuments?.termsOfService?.onLinkClick?.invoke()

        then()
        dev.mokkery.verify {
            fixture.deviceFacade.openUrlPreview("https://example.com/privacy")
            fixture.deviceFacade.openUrlPreview("https://example.com/terms")
        }
        assertFalse(sut.uiState.legalAcceptance.isChecked)
    }

    @Test
    fun `blocks signup while legal documents are loading`() = runUnitTest {
        given()
        val fixture = Fixture()
        val documents = CompletableDeferred<SignUpLegalDocuments>()
        everySuspend { fixture.getSignUpLegalDocuments() } calls { documents.await() }
        val sut = fixture.sut()

        whenn()
        sut.fillValidForm()

        then()
        assertFalse(sut.uiState.confirmButton.isEnabled)
        assertTrue(sut.uiState.legalAcceptance.isEnabled)
        assertTrue(sut.uiState.reloadLegalDocuments.isLoading)
        verifySuspend(VerifyMode.not) { fixture.startSignUp(any()) }
        documents.complete(fixture.documents)
    }

    @Test
    fun `keeps signup blocked after document loading fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSignUpLegalDocuments() } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.fillValidForm()

        then()
        assertFalse(sut.uiState.confirmButton.isEnabled)
        assertTrue(sut.uiState.legalAcceptance.isEnabled)
        assertEquals(ValidationState.ERROR, sut.uiState.legalAcceptance.validationState)
        assertEquals(
            AuthRes.strings.sign_up_legal_load_error.desc(),
            sut.uiState.legalAcceptance.supportingTextRes,
        )
        assertFalse(sut.uiState.reloadLegalDocuments.isLoading)
        assertTrue(sut.uiState.reloadLegalDocuments.isVisible)
        verifySuspend(VerifyMode.not) { fixture.startSignUp(any()) }
    }

    @Test
    fun `retries a failed document load`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSignUpLegalDocuments() } throws TestException()
        val sut = fixture.sut()
        everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents

        whenn()
        sut.uiState.reloadLegalDocuments.onClick?.invoke()

        then()
        assertEquals(ValidationState.DEFAULT, sut.uiState.legalAcceptance.validationState)
        assertNull(sut.uiState.legalAcceptance.supportingTextRes)
        assertTrue(sut.uiState.confirmButton.isEnabled)
        assertTrue(sut.uiState.legalAcceptance.isEnabled)
        assertFalse(sut.uiState.legalAcceptance.isChecked)
        assertFalse(sut.uiState.reloadLegalDocuments.isVisible)
        val documents = assertNotNull(sut.uiState.legalDocuments)
        assertEquals(fixture.documents.privacyPolicy, documents.privacyPolicy.document)
        assertEquals(fixture.documents.termsOfService, documents.termsOfService.document)
    }

    @Test
    fun `preserves the acceptance error while retrying document loading`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSignUpLegalDocuments() } throws TestException()
        val sut = fixture.sut()
        val documents = CompletableDeferred<SignUpLegalDocuments>()
        everySuspend { fixture.getSignUpLegalDocuments() } calls { documents.await() }

        whenn()
        sut.uiState.reloadLegalDocuments.onClick?.invoke()

        then()
        assertEquals(ValidationState.ERROR, sut.uiState.legalAcceptance.validationState)
        assertEquals(
            AuthRes.strings.sign_up_legal_load_error.desc(),
            sut.uiState.legalAcceptance.supportingTextRes,
        )
        assertTrue(sut.uiState.reloadLegalDocuments.isLoading)
        assertFalse(sut.uiState.confirmButton.isEnabled)
        documents.complete(fixture.documents)
    }

    @Test
    fun `requires renewed consent after either legal rejection`() = runUnitTest {
        listOf(
            FinishSignUp.Error.LegalDocumentVersionUnknown(),
            FinishSignUp.Error.LegalDocumentsNotAccepted(),
        ).forEach { error ->
            given()
            val fixture = Fixture()
            everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents
            everySuspend { fixture.startSignUp(any()) } throws error
            val sut = fixture.sut()
            sut.fillValidForm()
            sut.uiState.legalAcceptance.onCheckedChange?.invoke(true)
            val updated = fixture.documents.copy(
                privacyPolicy = fixture.documents.privacyPolicy.copy(version = "privacy-v3-new"),
                termsOfService = fixture.documents.termsOfService.copy(version = "terms-v2-new"),
            )
            everySuspend { fixture.getSignUpLegalDocuments() } returns updated

            whenn()
            sut.onConfirmButtonClick()

            then()
            assertFalse(sut.uiState.legalAcceptance.isChecked)
            assertTrue(sut.uiState.confirmButton.isEnabled)
            assertEquals(
                AuthRes.strings.sign_up_legal_documents_changed.desc(),
                sut.uiState.notification.assertSingle<PresentationNotification.Message>().message,
            )
            verifySuspend(VerifyMode.exactly(1)) { fixture.startSignUp(any()) }
            assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
        }
    }

    @Test
    fun `submits refreshed versions only after accepting again`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents
        everySuspend { fixture.startSignUp(any()) } throws
            FinishSignUp.Error.LegalDocumentVersionUnknown()
        val sut = fixture.sut()
        sut.fillValidForm()
        sut.uiState.legalAcceptance.onCheckedChange?.invoke(true)
        everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents.copy(
            privacyPolicy = fixture.documents.privacyPolicy.copy(version = "privacy-v3-new"),
            termsOfService = fixture.documents.termsOfService.copy(version = "terms-v2-new"),
        )
        sut.onConfirmButtonClick()
        everySuspend { fixture.startSignUp(capture(fixture.userData)) } returns fixture.pendingSignUp
        sut.uiState.legalAcceptance.onCheckedChange?.invoke(true)

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertEquals(
            AcceptedLegalDocuments("privacy-v3-new", "terms-v2-new"),
            fixture.userData.get().acceptedDocuments,
        )
    }

    @Test
    fun `blocks stale consent when reloading after rejection fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents
        everySuspend { fixture.startSignUp(any()) } throws
            FinishSignUp.Error.LegalDocumentsNotAccepted()
        val sut = fixture.sut()
        sut.fillValidForm()
        sut.uiState.legalAcceptance.onCheckedChange?.invoke(true)
        val previousDocuments = assertNotNull(sut.uiState.legalDocuments)
        everySuspend { fixture.getSignUpLegalDocuments() } throws TestException()

        whenn()
        sut.onConfirmButtonClick()

        then()
        assertFalse(sut.uiState.confirmButton.isEnabled)
        assertEquals(ValidationState.ERROR, sut.uiState.legalAcceptance.validationState)
        assertEquals(
            AuthRes.strings.sign_up_legal_load_error.desc(),
            sut.uiState.legalAcceptance.supportingTextRes,
        )
        assertSame(previousDocuments, sut.uiState.legalDocuments)
        assertTrue(sut.uiState.legalAcceptance.isEnabled)
    }

    @Test
    fun `does not start a second signup while one is in progress`() = runUnitTest {
        given()
        val fixture = Fixture()
        val created = CompletableDeferred<StartSignUp.PendingSignUp>()
        everySuspend { fixture.startSignUp(any()) } calls { created.await() }
        val sut = fixture.sut(acceptLegalDocuments = true)
        sut.fillValidForm()
        sut.onConfirmButtonClick()

        whenn()
        sut.uiState.email.onTextChanged?.invoke("other@example.com")
        sut.onConfirmButtonClick()

        then()
        assertFalse(sut.uiState.confirmButton.isEnabled)
        assertTrue(sut.uiState.legalAcceptance.isEnabled)
        verifySuspend(VerifyMode.exactly(1)) { fixture.startSignUp(any()) }
        created.complete(fixture.pendingSignUp)
    }

    @Test
    fun `acceptance callback submits the current document versions after reload`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents
        everySuspend { fixture.startSignUp(any()) } throws
            FinishSignUp.Error.LegalDocumentVersionUnknown()
        val sut = fixture.sut()
        sut.fillValidForm()
        val onAcceptanceChanged = assertNotNull(sut.uiState.legalAcceptance.onCheckedChange)
        sut.uiState.legalAcceptance.onCheckedChange?.invoke(true)
        everySuspend { fixture.getSignUpLegalDocuments() } returns fixture.documents.copy(
            privacyPolicy = fixture.documents.privacyPolicy.copy(version = "privacy-v3-new"),
            termsOfService = fixture.documents.termsOfService.copy(version = "terms-v2-new"),
        )
        sut.onConfirmButtonClick()
        everySuspend { fixture.startSignUp(capture(fixture.userData)) } returns fixture.pendingSignUp

        whenn()
        onAcceptanceChanged(true)
        sut.onConfirmButtonClick()

        then()
        assertEquals(
            AcceptedLegalDocuments("privacy-v3-new", "terms-v2-new"),
            fixture.userData.get().acceptedDocuments,
        )
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `drops reloads queued before document loading starts`() = runUnitTest {
        given()
        val queuedDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(queuedDispatcher)
        DispatcherProvider.swapMain(queuedDispatcher)
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.reloadLegalDocuments.onClick?.invoke()
        sut.uiState.reloadLegalDocuments.onClick?.invoke()
        runCurrent()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.getSignUpLegalDocuments() }
        assertNotNull(sut.uiState.legalDocuments)
        assertFalse(sut.uiState.reloadLegalDocuments.isLoading)
    }

    @Test
    fun `preserves documents and consent while reloading`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut(acceptLegalDocuments = true)
        val previousDocuments = assertNotNull(sut.uiState.legalDocuments)
        val documents = CompletableDeferred<SignUpLegalDocuments>()
        everySuspend { fixture.getSignUpLegalDocuments() } calls { documents.await() }

        whenn()
        sut.uiState.reloadLegalDocuments.onClick?.invoke()

        then()
        assertSame(previousDocuments, sut.uiState.legalDocuments)
        assertTrue(sut.uiState.reloadLegalDocuments.isLoading)
        assertTrue(sut.uiState.legalAcceptance.isEnabled)
        assertTrue(sut.uiState.legalAcceptance.isChecked)
        assertFalse(sut.uiState.confirmButton.isEnabled)
        documents.complete(fixture.documents)
    }
}
