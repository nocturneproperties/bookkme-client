package me.bookk.feature.authorization.data.local

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import kotlin.test.Test
import kotlin.test.assertEquals

class PasskeyAssertionTest {

    @Test
    fun `reads credential id and user handle from the assertion`() = runUnitTest {
        given()
        val payload = PasskeyVerificationPayload(
            """{"id":"Y3JlZC1pZA","rawId":"Y3JlZC1pZA","type":"public-key","response":{"userHandle":"dXNlci1pZA","signature":"c2ln"}}"""
        )

        whenn()
        val result = PasskeyAssertion.from(payload)

        then()
        assertEquals(PasskeyAssertion(credentialId = "Y3JlZC1pZA", userHandle = "dXNlci1pZA"), result)
    }

    @Test
    fun `has no user handle when the assertion omits it`() = runUnitTest {
        given()
        val payload = PasskeyVerificationPayload("""{"id":"Y3JlZC1pZA","response":{}}""")

        whenn()
        val result = PasskeyAssertion.from(payload)

        then()
        assertEquals(PasskeyAssertion(credentialId = "Y3JlZC1pZA", userHandle = null), result)
    }

    @Test
    fun `has no user handle when the assertion carries an empty one`() = runUnitTest {
        given()
        val payload = PasskeyVerificationPayload("""{"id":"Y3JlZC1pZA","response":{"userHandle":""}}""")

        whenn()
        val result = PasskeyAssertion.from(payload)

        then()
        assertEquals(PasskeyAssertion(credentialId = "Y3JlZC1pZA", userHandle = null), result)
    }
}
