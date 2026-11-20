package me.bookk.feature.authorization.data.local

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import kotlin.test.Test
import kotlin.test.assertFailsWith

class JsPassKeyManagerTest {

    private class Fixture {
        val sut = JsPassKeyManager()
    }

    @Test
    fun `refuses to create a passkey on the web`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = PassKeyManager.CreationRequest("user", "Ann", "{}", "challenge")

        whenn()
        then()
        assertFailsWith<UnsupportedOperationException> { fixture.sut.create(request) }
    }

    @Test
    fun `refuses to authorize with a passkey on the web`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = PassKeyManager.AuthorizationRequest("{}", "challenge")

        whenn()
        then()
        assertFailsWith<UnsupportedOperationException> { fixture.sut.authorize(request) }
    }

    @Test
    fun `refuses to signal a deleted account on the web`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        then()
        assertFailsWith<UnsupportedOperationException> {
            fixture.sut.signalAccountDeleted(PasskeyVerificationPayload("{}"))
        }
    }
}
