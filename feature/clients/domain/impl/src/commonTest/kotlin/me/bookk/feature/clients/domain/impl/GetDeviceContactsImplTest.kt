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
import me.bookk.feature.clients.domain.datasource.DeviceContactsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
    }

    @Test
    fun `returns contacts read from the device`() = runUnitTest {
        given()
        val fixture = Fixture()
        val contacts = listOf(
            ClientImportEntry(name = "John", lastName = "Doe", phone = "+48123456789", email = null),
            ClientImportEntry(name = "Anna", lastName = "", phone = null, email = "anna@example.com")
        )
        everySuspend { fixture.dataSource.getContacts() } returns contacts

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(contacts, result)
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
}
