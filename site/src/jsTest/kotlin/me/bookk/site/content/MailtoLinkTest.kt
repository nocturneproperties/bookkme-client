package me.bookk.site.content

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class MailtoLinkTest {

    @Test
    fun `adds the subject to the address`() = runUnitTest {
        given()
        val subject = "Feedback"

        whenn()
        val link = MailtoLink.build(EMAIL, subject)

        then()
        assertEquals("mailto:$EMAIL?subject=Feedback", link)
    }

    @Test
    fun `encodes spaces and reserved characters in the subject`() = runUnitTest {
        given()
        val subject = "Sign-in & passkeys?"

        whenn()
        val link = MailtoLink.build(EMAIL, subject)

        then()
        assertEquals("mailto:$EMAIL?subject=Sign-in%20%26%20passkeys%3F", link)
    }

    @Test
    fun `encodes non-latin letters in the subject`() = runUnitTest {
        given()
        val subject = "Вхід"

        whenn()
        val link = MailtoLink.build(EMAIL, subject)

        then()
        assertEquals("mailto:$EMAIL?subject=%D0%92%D1%85%D1%96%D0%B4", link)
    }

    @Test
    fun `leaves the subject out when it is blank`() = runUnitTest {
        given()
        val subject = " "

        whenn()
        val link = MailtoLink.build(EMAIL, subject)

        then()
        assertEquals("mailto:$EMAIL", link)
    }

    private companion object {
        const val EMAIL = "support@bookkme.com"
    }
}
