package build_src.site

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SecurityTxtExpiryTest {
    private val now = Instant.parse("2026-11-17T12:00:00Z")

    private fun securityTxt(expires: String): String {
        return "Contact: mailto:security@bookkme.com\nExpires: $expires\n"
    }

    @Test
    fun `accepts an expiry between a month and a year away`() {
        val content = securityTxt("2027-06-01T00:00:00.000Z")

        val error = SecurityTxtExpiry.error(content, now)

        assertNull(error)
    }

    @Test
    fun `accepts an expiry exactly thirty days away`() {
        val content = securityTxt("2026-12-17T12:00:00Z")

        val error = SecurityTxtExpiry.error(content, now)

        assertNull(error)
    }

    @Test
    fun `rejects an expiry less than thirty days away`() {
        val content = securityTxt("2026-12-17T11:59:59Z")

        val error = SecurityTxtExpiry.error(content, now)

        assertNotNull(error)
        assertTrue("less than 30 days away" in error)
    }

    @Test
    fun `rejects an expiry in the past`() {
        val content = securityTxt("2026-11-01T00:00:00Z")

        val error = SecurityTxtExpiry.error(content, now)

        assertNotNull(error)
        assertTrue("less than 30 days away" in error)
    }

    @Test
    fun `rejects an expiry more than a year away`() {
        val content = securityTxt("2027-11-17T12:00:01Z")

        val error = SecurityTxtExpiry.error(content, now)

        assertNotNull(error)
        assertTrue("at most 365 days ahead" in error)
    }

    @Test
    fun `rejects a file without an expiry`() {
        val content = "Contact: mailto:security@bookkme.com\n"

        val error = SecurityTxtExpiry.error(content, now)

        assertEquals("expected exactly one 'Expires:' field (RFC 9116), found 0", error)
    }

    @Test
    fun `rejects a file with two expiry fields`() {
        val content = securityTxt("2027-06-01T00:00:00Z") + "Expires: 2027-07-01T00:00:00Z\n"

        val error = SecurityTxtExpiry.error(content, now)

        assertEquals("expected exactly one 'Expires:' field (RFC 9116), found 2", error)
    }

    @Test
    fun `rejects an expiry that is not an RFC 3339 timestamp`() {
        val content = securityTxt("2027-06-01")

        val error = SecurityTxtExpiry.error(content, now)

        assertEquals("'Expires: 2027-06-01' is not an RFC 3339 timestamp", error)
    }
}
