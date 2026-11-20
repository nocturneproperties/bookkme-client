package me.bookk.feature.authorization.presentation.screen.telegram

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentially
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CompletableDeferred
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.feature.authorization.domain.api.GetSignUpLegalDocuments
import me.bookk.feature.authorization.domain.api.IsUserLoggedIn
import me.bookk.feature.authorization.domain.api.SignInWithTelegram
import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocuments
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TelegramSignInViewModelTest {

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
        val signInWithTelegram = mock<SignInWithTelegram> {
            everySuspend { invoke(any(), any()) } returns Unit
        }
        val isUserLoggedIn = mock<IsUserLoggedIn> {
            everySuspend { invoke() } returns false
        }
        val getSignUpLegalDocuments = mock<GetSignUpLegalDocuments> {
            everySuspend { refresh() } returns LEGAL_DOCUMENTS
        }
        val errorMapper = FakeErrorMapper()

        fun sut(): TelegramSignInViewModel {
            return TelegramSignInViewModel(
                initData = INIT_DATA,
                signInWithTelegram = signInWithTelegram,
                isUserLoggedIn = isUserLoggedIn,
                getSignUpLegalDocuments = getSignUpLegalDocuments,
                stateFactory = FakeTelegramSignInStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }

        fun requireLegalAcceptance() {
            everySuspend { signInWithTelegram(INIT_DATA, null) } throws
                SignInWithTelegram.Error.LegalDocumentsNotAccepted()
        }
    }

    private fun TelegramSignInViewModel.acceptLegalDocuments() {
        uiState.legalAcceptance.onCheckedChange?.invoke(true)
        uiState.continueButton.onClick?.invoke()
    }

    private fun TelegramSignInViewModel.assertFailure(
        expected: TelegramSignInFailure,
        canRetry: Boolean,
    ) {
        assertEquals(TelegramSignInPhase.FAILED, uiState.phase)
        assertEquals(expected.message, uiState.message)
        assertEquals(canRetry, uiState.retryButton.isVisible)
    }

    private enum class TelegramSignInFailure(
        val message: StringDesc,
    ) {
        EXPIRED(AuthRes.strings.telegram_sign_in_expired.desc()),
        INVALID(AuthRes.strings.telegram_sign_in_invalid.desc()),
        UNAVAILABLE(AuthRes.strings.telegram_sign_in_unavailable.desc()),
        ACCOUNT_CREATION(AuthRes.strings.telegram_sign_in_account_creation_failed.desc()),
        LEGAL_LOAD(AuthRes.strings.sign_up_legal_load_error.desc()),
    }

    @Test
    fun `signs in without accepted documents when opened`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.signInWithTelegram(INIT_DATA, null) }
        assertEquals(TelegramSignInPhase.SIGNED_IN, sut.uiState.phase)
        assertEquals(AuthRes.strings.telegram_sign_in_signed_in.desc(), sut.uiState.message)
    }

    @Test
    fun `skips telegram sign-in when the user is already signed in`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.isUserLoggedIn() } returns true

        whenn()
        val sut = fixture.sut()

        then()
        verifySuspend(VerifyMode.not) { fixture.signInWithTelegram(any(), any()) }
        assertEquals(TelegramSignInPhase.SIGNED_IN, sut.uiState.phase)
    }

    @Test
    fun `shows the signing in message while the sign-in runs`() = runUnitTest {
        given()
        val fixture = Fixture()
        val signedIn = CompletableDeferred<Unit>()
        everySuspend { fixture.signInWithTelegram(any(), any()) } calls { signedIn.await() }

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(TelegramSignInPhase.SIGNING_IN, sut.uiState.phase)
        assertEquals(AuthRes.strings.telegram_sign_in_progress.desc(), sut.uiState.message)
        signedIn.complete(Unit)
    }

    @Test
    fun `asks a new telegram user to accept the current legal documents`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.requireLegalAcceptance()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(TelegramSignInPhase.LEGAL_ACCEPTANCE, sut.uiState.phase)
        assertEquals(AuthRes.strings.telegram_sign_in_legal_prompt.desc(), sut.uiState.message)
        assertEquals(PRIVACY_POLICY.url, sut.uiState.legalDocuments?.privacyPolicy?.linkUrl)
        assertEquals(TERMS_OF_SERVICE.url, sut.uiState.legalDocuments?.termsOfService?.linkUrl)
        assertEquals(
            AuthRes.strings.sign_up_accept_legal_documents.desc(),
            sut.uiState.legalAcceptance.text,
        )
    }

    @Test
    fun `keeps continue disabled until the legal documents are accepted`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.requireLegalAcceptance()
        val sut = fixture.sut()
        val disabledBeforeAcceptance = !sut.uiState.continueButton.isEnabled

        whenn()
        sut.uiState.legalAcceptance.onCheckedChange?.invoke(true)

        then()
        assertTrue(disabledBeforeAcceptance)
        assertTrue(sut.uiState.legalAcceptance.isChecked)
        assertTrue(sut.uiState.continueButton.isEnabled)
    }

    @Test
    fun `signs in with the accepted document versions`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.requireLegalAcceptance()
        val accepted = AcceptedLegalDocuments(
            privacyPolicy = PRIVACY_POLICY.version,
            termsOfService = TERMS_OF_SERVICE.version,
        )
        everySuspend { fixture.signInWithTelegram(INIT_DATA, accepted) } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.acceptLegalDocuments()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.signInWithTelegram(INIT_DATA, accepted) }
        assertEquals(TelegramSignInPhase.SIGNED_IN, sut.uiState.phase)
    }

    @Test
    fun `asks again for acceptance when the documents changed meanwhile`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.signInWithTelegram(any(), any()) } sequentially {
            throws(SignInWithTelegram.Error.LegalDocumentsNotAccepted())
            throws(SignInWithTelegram.Error.LegalDocumentVersionUnknown())
        }
        val sut = fixture.sut()

        whenn()
        sut.acceptLegalDocuments()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.getSignUpLegalDocuments.refresh() }
        assertEquals(TelegramSignInPhase.LEGAL_ACCEPTANCE, sut.uiState.phase)
        assertEquals(AuthRes.strings.sign_up_legal_documents_changed.desc(), sut.uiState.message)
        assertFalse(sut.uiState.legalAcceptance.isChecked)
        assertFalse(sut.uiState.continueButton.isEnabled)
    }

    @Test
    fun `offers a retry when the legal documents fail to load`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.requireLegalAcceptance()
        everySuspend { fixture.getSignUpLegalDocuments.refresh() } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        sut.assertFailure(TelegramSignInFailure.LEGAL_LOAD, canRetry = true)
    }

    @Test
    fun `asks to reopen the mini app when the telegram data expired`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.signInWithTelegram(any(), any()) } throws
            SignInWithTelegram.Error.TelegramDataExpired()

        whenn()
        val sut = fixture.sut()

        then()
        sut.assertFailure(TelegramSignInFailure.EXPIRED, canRetry = false)
    }

    @Test
    fun `asks to reopen the mini app when telegram cannot confirm the user`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.signInWithTelegram(any(), any()) } throws
            SignInWithTelegram.Error.TelegramDataInvalid()

        whenn()
        val sut = fixture.sut()

        then()
        sut.assertFailure(TelegramSignInFailure.INVALID, canRetry = false)
    }

    @Test
    fun `offers a retry when telegram sign-in is unavailable`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.signInWithTelegram(any(), any()) } throws
            SignInWithTelegram.Error.Unavailable()

        whenn()
        val sut = fixture.sut()

        then()
        sut.assertFailure(TelegramSignInFailure.UNAVAILABLE, canRetry = true)
    }

    @Test
    fun `offers a retry when the account could not be created`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.signInWithTelegram(any(), any()) } throws
            SignInWithTelegram.Error.AccountCreationFailed()

        whenn()
        val sut = fixture.sut()

        then()
        sut.assertFailure(TelegramSignInFailure.ACCOUNT_CREATION, canRetry = true)
    }

    @Test
    fun `describes an unexpected failure and offers a retry`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.signInWithTelegram(any(), any()) } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(TelegramSignInPhase.FAILED, sut.uiState.phase)
        assertEquals("message of TestException".desc(), sut.uiState.message)
        assertTrue(sut.uiState.retryButton.isVisible)
    }

    @Test
    fun `retries the sign-in from the start`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.signInWithTelegram(any(), any()) } sequentially {
            throws(SignInWithTelegram.Error.Unavailable())
            returns(Unit)
        }
        val sut = fixture.sut()

        whenn()
        sut.uiState.retryButton.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.signInWithTelegram(INIT_DATA, null) }
        assertEquals(TelegramSignInPhase.SIGNED_IN, sut.uiState.phase)
    }

    private companion object {
        const val INIT_DATA = "query_id=AAE&user=%7B%22id%22%3A1%7D&auth_date=1&hash=abc"
        val PRIVACY_POLICY = SignUpLegalDocument(
            SignUpLegalDocument.Document.PRIVACY_POLICY,
            "privacy-v3",
            "https://bookkme.app/privacy/3",
        )
        val TERMS_OF_SERVICE = SignUpLegalDocument(
            SignUpLegalDocument.Document.TERMS_OF_SERVICE,
            "terms-v2",
            "https://bookkme.app/terms/2",
        )
        val LEGAL_DOCUMENTS = SignUpLegalDocuments(PRIVACY_POLICY, TERMS_OF_SERVICE)
    }
}
