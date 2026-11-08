package me.bookk.feature.authorization.data.datasource

import kotlinx.coroutines.flow.first
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.datasource.registration.ServerSignUpChallenge
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSession
import me.bookk.feature.authorization.domain.entity.EmailVerification
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlin.uuid.Uuid

class InMemorySignUpSessionDataSourceTest {

    private fun session(attestation: PasskeyVerificationPayload? = null): SignUpSession {
        return SignUpSession(
            userData = StartSignUp.UserData("John", "Doe", "john@example.com"),
            challenge = ServerSignUpChallenge(
                requestId = "req",
                displayName = "John Doe",
                jsonChallengeData = "{}",
                challenge = "chal",
                userHandle = "handle",
                emailVerification = EmailVerification(
                    Uuid.parse("00000000-0000-0000-0000-0000000000a1"),
                    Instant.fromEpochSeconds(1_000),
                    Instant.fromEpochSeconds(100),
                ),
            ),
            attestation = attestation,
        )
    }

    @Test
    fun `has no session initially`() = runUnitTest {
        given()
        val sut = InMemorySignUpSessionDataSource()

        whenn()
        val result = sut.get()

        then()
        assertNull(result)
    }

    @Test
    fun `returns the last saved session`() = runUnitTest {
        given()
        val sut = InMemorySignUpSessionDataSource()
        sut.save(session())
        val withAttestation = session(PasskeyVerificationPayload("{}"))

        whenn()
        sut.save(withAttestation)

        then()
        assertEquals(withAttestation, sut.get())
    }

    @Test
    fun `clears the session on logout`() = runUnitTest {
        given()
        val sut = InMemorySignUpSessionDataSource()
        sut.save(session())

        whenn()
        sut.doOnLogOut()

        then()
        assertNull(sut.get())
    }

    @Test
    fun `emits the current session to observers`() = runUnitTest {
        given()
        val sut = InMemorySignUpSessionDataSource()
        val saved = session()

        whenn()
        sut.save(saved)

        then()
        assertEquals(saved, sut.observe().first())
    }

    @Test
    fun `emits no session to observers after logout`() = runUnitTest {
        given()
        val sut = InMemorySignUpSessionDataSource()
        sut.save(session())

        whenn()
        sut.doOnLogOut()

        then()
        assertNull(sut.observe().first())
    }
}
