package me.bookk.feature.clients.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry
import me.bookk.feature.clients.domain.datasource.DeviceContact
import me.bookk.feature.clients.domain.datasource.DeviceContactsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class GetDeviceContactsImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture {
        val dataSource = mock<DeviceContactsDataSource>()
        val sut = GetDeviceContactsImpl(dataSource)

        fun stubContacts(vararg contacts: DeviceContact) {
            everySuspend { dataSource.getContacts() } returns contacts.toList()
        }
    }

    @Test
    fun `returns name, last name, first phone and first email of each contact`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubContacts(
            DeviceContact(
                name = "John",
                lastName = "Doe",
                phones = listOf("+48123456789", "+48987654321"),
                emails = listOf("john@example.com", "doe@example.com")
            )
        )

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(
            listOf(ClientImportEntry(name = "John", lastName = "Doe", phone = "+48123456789", email = "john@example.com")),
            result
        )
    }

    @Test
    fun `removes spaces, dashes and parentheses from phone numbers`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubContacts(stubContact(phones = listOf("(555) 123-45 67")))

        whenn()
        val result = fixture.sut()

        then()
        assertEquals("5551234567", result.single().phone)
    }

    @Test
    fun `keeps the plus sign of international phone numbers`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubContacts(stubContact(phones = listOf("‪+48 500-123-456‬")))

        whenn()
        val result = fixture.sut()

        then()
        assertEquals("+48500123456", result.single().phone)
    }

    @Test
    fun `drops a plus sign that is not at the start of a phone number`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubContacts(stubContact(phones = listOf("500+123456")))

        whenn()
        val result = fixture.sut()

        then()
        assertEquals("500123456", result.single().phone)
    }

    @Test
    fun `cuts the extension off a phone number`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubContacts(stubContact(phones = listOf("500-123-456 ext. 12")))

        whenn()
        val result = fixture.sut()

        then()
        assertEquals("500123456", result.single().phone)
    }

    @Test
    fun `cuts pause and wait digits off a phone number`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubContacts(
            stubContact(phones = listOf("+48500123456,,123")),
            stubContact(phones = listOf("+48500123456;123"))
        )

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(listOf("+48500123456", "+48500123456"), result.map { it.phone })
    }

    @Test
    fun `converts non-latin digits of a phone number to latin digits`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubContacts(stubContact(phones = listOf("٥٠٠ １２３")))

        whenn()
        val result = fixture.sut()

        then()
        assertEquals("500123", result.single().phone)
    }

    @Test
    fun `uses the first phone number that has digits`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubContacts(stubContact(phones = listOf("n/a", "( - )", "+48 500 123 456")))

        whenn()
        val result = fixture.sut()

        then()
        assertEquals("+48500123456", result.single().phone)
    }

    @Test
    fun `keeps a contact with an email when none of its phone numbers has digits`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubContacts(stubContact(phones = listOf("( - )"), emails = listOf("john@example.com")))

        whenn()
        val result = fixture.sut()

        then()
        assertNull(result.single().phone)
        assertEquals("john@example.com", result.single().email)
    }

    @Test
    fun `uses the first email that is not blank`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubContacts(stubContact(emails = listOf(" ", "john@example.com")))

        whenn()
        val result = fixture.sut()

        then()
        assertEquals("john@example.com", result.single().email)
    }

    @Test
    fun `drops contacts without a phone number and an email`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubContacts(
            stubContact(name = "Anna", phones = listOf("( - )"), emails = listOf(" ")),
            stubContact(name = "John", phones = listOf("+48500123456"))
        )

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(listOf("John"), result.map { it.name })
    }

    @Test
    fun `rethrows when reading device contacts fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getContacts() } throws IllegalStateException()

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }

    private fun stubContact(
        name: String = "John",
        phones: List<String> = emptyList(),
        emails: List<String> = emptyList()
    ): DeviceContact {
        return DeviceContact(name = name, lastName = "Doe", phones = phones, emails = emails)
    }
}
