package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.ConfirmEmailChange
import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.email.EmailChangeDataSource
import me.bookk.feature.authorization.domain.datasource.profile.UserProfileDataSource
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import me.bookk.feature.authorization.domain.entity.UserProfile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class ConfirmEmailChangeImplTest {

    private class Fixture {
        val emailChangeDataSource = mock<EmailChangeDataSource>()
        val userProfileDataSource = mock<UserProfileDataSource>()
        val sut = ConfirmEmailChangeImpl(emailChangeDataSource, userProfileDataSource)
        val change = StartEmailChange.PendingEmailChange("new@example.com", stubEmailVerification())
        val profile = UserProfile(
            id = Uuid.parse("00000000-0000-0000-0000-0000000000c3"),
            firstName = "Anna",
            lastName = "Smith",
            email = "old@example.com",
            phone = "+48100200300",
        )

        fun stubConfirmed() {
            everySuspend { emailChangeDataSource.confirmEmailChange(any(), any()) } returns Unit
        }

        fun stubRefused(code: Int) {
            everySuspend { emailChangeDataSource.confirmEmailChange(any(), any()) } throws
                Error.BusinessError(code, "rejected")
        }

        fun stubCachedProfile(cached: UserProfile?) {
            everySuspend { userProfileDataSource.getProfileFromDatabase() } returns cached
            everySuspend { userProfileDataSource.upsertProfile(any()) } returns Unit
        }
    }

    @Test
    fun `confirms the verification with the code`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubConfirmed()
        fixture.stubCachedProfile(fixture.profile)

        whenn()
        fixture.sut(fixture.change, "123456")

        then()
        verifySuspend { fixture.emailChangeDataSource.confirmEmailChange(stubVerificationId, "123456") }
    }

    @Test
    fun `stores the confirmed address in the cached profile`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubConfirmed()
        fixture.stubCachedProfile(fixture.profile)

        whenn()
        fixture.sut(fixture.change, "123456")

        then()
        verifySuspend {
            fixture.userProfileDataSource.upsertProfile(fixture.profile.copy(email = "new@example.com"))
        }
    }

    @Test
    fun `stores nothing when no profile is cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubConfirmed()
        fixture.stubCachedProfile(null)

        whenn()
        fixture.sut(fixture.change, "123456")

        then()
        verifySuspend(VerifyMode.not) { fixture.userProfileDataSource.upsertProfile(any()) }
    }

    @Test
    fun `keeps the cached profile when the code is refused`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(AuthErrorCodes.EMAIL_CODE_INVALID)
        fixture.stubCachedProfile(fixture.profile)

        whenn()
        runCatching { fixture.sut(fixture.change, "000000") }

        then()
        verifySuspend(VerifyMode.not) { fixture.userProfileDataSource.upsertProfile(any()) }
    }

    @Test
    fun `maps error 910001 to CodeInvalid`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(AuthErrorCodes.EMAIL_CODE_INVALID)

        whenn()
        val result = runCatching { fixture.sut(fixture.change, "000000") }

        then()
        assertFailsWith<EmailVerificationError.CodeInvalid> { result.getOrThrow() }
    }

    @Test
    fun `maps error 910002 to CodeExpired`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(AuthErrorCodes.EMAIL_CODE_EXPIRED)

        whenn()
        val result = runCatching { fixture.sut(fixture.change, "000000") }

        then()
        assertFailsWith<EmailVerificationError.CodeExpired> { result.getOrThrow() }
    }

    @Test
    fun `maps error 910003 to AttemptsExceeded`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(AuthErrorCodes.EMAIL_CODE_ATTEMPTS_EXCEEDED)

        whenn()
        val result = runCatching { fixture.sut(fixture.change, "000000") }

        then()
        assertFailsWith<EmailVerificationError.AttemptsExceeded> { result.getOrThrow() }
    }

    @Test
    fun `maps error 1 to EmailAlreadyExist`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(AuthErrorCodes.EMAIL_EXIST)

        whenn()
        val result = runCatching { fixture.sut(fixture.change, "123456") }

        then()
        assertFailsWith<ConfirmEmailChange.Error.EmailAlreadyExist> { result.getOrThrow() }
    }

    @Test
    fun `rethrows unrelated business errors`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRefused(777)

        whenn()
        val result = runCatching { fixture.sut(fixture.change, "123456") }

        then()
        assertEquals(777, assertFailsWith<Error.BusinessError> { result.getOrThrow() }.errorCode)
    }
}
