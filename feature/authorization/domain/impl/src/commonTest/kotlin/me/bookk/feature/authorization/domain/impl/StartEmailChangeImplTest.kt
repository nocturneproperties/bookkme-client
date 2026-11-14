package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.email.EmailChangeDataSource
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StartEmailChangeImplTest {

    private class Fixture {
        val emailChangeDataSource = mock<EmailChangeDataSource>()
        val sut = StartEmailChangeImpl(emailChangeDataSource)

        fun stubStarted() {
            everySuspend { emailChangeDataSource.startEmailChange(any()) } returns
                stubEmailVerification()
        }

        fun stubRefused(code: Int) {
            everySuspend { emailChangeDataSource.startEmailChange(any()) } throws
                Error.BusinessError(code, "rejected")
        }
    }

    @Test
    fun `returns the new address with its verification`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubStarted()

        whenn()
        val result = fixture.sut("new@example.com")

        then()
        assertEquals("new@example.com", result.email)
        assertEquals(stubEmailVerification(), result.verification)
    }

    @Test
    fun `requests the change for the trimmed address`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubStarted()

        whenn()
        val result = fixture.sut("  new@example.com\n")

        then()
        verifySuspend { fixture.emailChangeDataSource.startEmailChange("new@example.com") }
        assertEquals("new@example.com", result.email)
    }

    @Test
    fun `maps error 1 to EmailAlreadyExist`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(AuthErrorCodes.EMAIL_EXIST)

        whenn()
        val result = runCatching { fixture.sut("new@example.com") }

        then()
        assertFailsWith<StartEmailChange.Error.EmailAlreadyExist> { result.getOrThrow() }
    }

    @Test
    fun `maps error 2 to InvalidEmailFormat`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(AuthErrorCodes.INVALID_EMAIL_FORMAT)

        whenn()
        val result = runCatching { fixture.sut("new@example.com") }

        then()
        assertFailsWith<StartEmailChange.Error.InvalidEmailFormat> { result.getOrThrow() }
    }

    @Test
    fun `maps error 910005 to Undeliverable`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(AuthErrorCodes.EMAIL_UNDELIVERABLE)

        whenn()
        val result = runCatching { fixture.sut("new@example.com") }

        then()
        assertFailsWith<EmailVerificationError.Undeliverable> { result.getOrThrow() }
    }

    @Test
    fun `maps error 910004 to ResendTooSoon`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(AuthErrorCodes.EMAIL_CODE_RESEND_TOO_SOON)

        whenn()
        val result = runCatching { fixture.sut("new@example.com") }

        then()
        assertFailsWith<EmailVerificationError.ResendTooSoon> { result.getOrThrow() }
    }

    @Test
    fun `rethrows unrelated business errors`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(777)

        whenn()
        val result = runCatching { fixture.sut("new@example.com") }

        then()
        assertEquals(777, assertFailsWith<Error.BusinessError> { result.getOrThrow() }.errorCode)
    }
}
