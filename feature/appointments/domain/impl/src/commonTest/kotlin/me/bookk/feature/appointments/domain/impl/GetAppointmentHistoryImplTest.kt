package me.bookk.feature.appointments.domain.impl

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.domain.pagination.CounterPager
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetAppointmentHistoryImplTest {

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
        val sut = GetAppointmentHistoryImpl(dataSource)
    }

    @Test
    fun `reload fetches first page with offset 0`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val firstPage = List(20) { stubAppointment() }
        coEvery { fixture.dataSource.getAppointmentHistory(businessId, 20, 0L, null) } returns firstPage

        whenn()
        val result = fixture.fixture.reload(businessId)

        then()
        assertEquals(firstPage, result)
    }

    @Test
    fun `loadMore fetches next page with correct offset`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val firstPage = List(20) { stubAppointment() }
        val secondPage = List(10) { stubAppointment() }
        coEvery { fixture.dataSource.getAppointmentHistory(businessId, 20, 0L, null) } returns firstPage
        coEvery { fixture.dataSource.getAppointmentHistory(businessId, 20, 20L, null) } returns secondPage
        fixture.fixture.reload(businessId)

        whenn()
        val result = fixture.fixture.loadMore()

        then()
        assertEquals(secondPage, result)
    }

    @Test
    fun `loadMore returns empty when all data loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val partial = List(5) { stubAppointment() }
        coEvery { fixture.dataSource.getAppointmentHistory(businessId, 20, 0L, null) } returns partial
        fixture.fixture.reload(businessId)

        whenn()
        val result = fixture.fixture.loadMore()

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `loadMore throws when called before reload`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        then()
        assertFailsWith<CounterPager.CounterPagerException> {
            fixture.fixture.loadMore()
        }
    }

    @Test
    fun `reload passes query to datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val query = "John"
        val page = listOf(stubAppointment())
        coEvery { fixture.dataSource.getAppointmentHistory(businessId, 20, 0L, query) } returns page

        whenn()
        val result = fixture.fixture.reload(businessId, query)

        then()
        assertEquals(page, result)
    }
}
