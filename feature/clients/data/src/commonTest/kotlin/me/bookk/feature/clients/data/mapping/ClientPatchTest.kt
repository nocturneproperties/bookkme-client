package me.bookk.feature.clients.data.mapping

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class ClientPatchTest {
    private val id = Uuid.parse("00000000-0000-0000-0000-000000000001")

    @Test
    fun `integrated client patch only includes the description`() = runUnitTest {
        given()
        val client = Client.Integrated(id, "John", "Doe", "123", "john@example.com", id, id, "Note")

        whenn()
        val result = client.toUpdateRemote()

        then()
        assertEquals(id, result.id)
        assertEquals("Note", result.description)
        assertNull(result.name)
        assertNull(result.lastName)
        assertNull(result.phone)
        assertNull(result.email)
    }

    @Test
    fun `detached client patch retains editable personal details`() = runUnitTest {
        given()
        val client = Client.Detached(id, "John", "Doe", "123", "john@example.com", id, "Note")

        whenn()
        val result = client.toUpdateRemote()

        then()
        assertEquals("John", result.name)
        assertEquals("Doe", result.lastName)
        assertEquals("123", result.phone)
        assertEquals("john@example.com", result.email)
        assertEquals("Note", result.description)
    }
}
