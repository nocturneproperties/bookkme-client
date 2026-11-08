package me.bookk.feature.authorization.domain.impl

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSession
import me.bookk.feature.authorization.domain.entity.SignUpRejection
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveSignUpRejectionImplTest {

    private class Fixture {
        val sessionDataSource = InMemorySignUpSessionDataSource()
        val sut = ObserveSignUpRejectionImpl(sessionDataSource)
    }

    private fun session(rejection: SignUpRejection? = null): SignUpSession {
        return SignUpSession(
            userData = stubSignUpUserData(),
            challenge = if (rejection == null) stubSignUpChallenge() else null,
            rejection = rejection,
        )
    }

    @Test
    fun `emits no rejection without a session`() = runUnitTest {
        given()
        val fixture = Fixture()
        val values = mutableListOf<SignUpRejection?>()

        whenn()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut().toList(values)
        }

        then()
        assertEquals(listOf<SignUpRejection?>(null), values)
    }

    @Test
    fun `emits the rejection of the pending sign up`() = runUnitTest {
        given()
        val fixture = Fixture()
        val values = mutableListOf<SignUpRejection?>()
        fixture.sessionDataSource.save(session())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut().toList(values)
        }

        whenn()
        fixture.sessionDataSource.save(session(SignUpRejection.EMAIL_TAKEN))

        then()
        assertEquals(listOf(null, SignUpRejection.EMAIL_TAKEN), values)
    }

    @Test
    fun `clears the rejection once sign up starts again`() = runUnitTest {
        given()
        val fixture = Fixture()
        val values = mutableListOf<SignUpRejection?>()
        fixture.sessionDataSource.save(session(SignUpRejection.LEGAL_DOCUMENTS_CHANGED))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut().toList(values)
        }

        whenn()
        fixture.sessionDataSource.save(session())

        then()
        assertEquals(listOf(SignUpRejection.LEGAL_DOCUMENTS_CHANGED, null), values)
    }

    @Test
    fun `does not repeat an unchanged rejection`() = runUnitTest {
        given()
        val fixture = Fixture()
        val values = mutableListOf<SignUpRejection?>()
        fixture.sessionDataSource.save(session())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut().toList(values)
        }

        whenn()
        fixture.sessionDataSource.save(
            session().copy(attestation = PasskeyVerificationPayload("{}")),
        )

        then()
        assertEquals(listOf<SignUpRejection?>(null), values)
    }
}
