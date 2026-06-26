package me.bookk.feature.appointments.domain.impl

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetAppointmentImplTest {

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
        val dataSource = mockk<AppointmentDataSource>()
        val sut = GetAppointmentImpl(dataSource)
    }

    @Test
    fun `returns appointment by id`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        val expected = stubAppointment(id = id)
        coEvery { fixture.dataSource.getAppointment(id) } returns expected

        whenn()
        val result = fixture.sut(id)

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `propagates datasource exception`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        coEvery { fixture.dataSource.getAppointment(id) } throws IllegalStateException("not found")

        whenn()
        val thrown = runCatching { fixture.sut(id) }.exceptionOrNull()

        then()
        assert(thrown is IllegalStateException)
    }
}
