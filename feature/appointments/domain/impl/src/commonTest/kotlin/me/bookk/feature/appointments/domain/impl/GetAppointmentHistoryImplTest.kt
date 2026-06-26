package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
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
        val dataSource = mock<AppointmentDataSource>()
        val sut = GetAppointmentHistoryImpl(dataSource)
    }

    @Test
    fun `reload fetches first page with offset 0`() = runUnitTest {
        given()
        val sut = Fixture()
        val businessId = Uuid.random()
        val firstPage = List(20) { stubAppointment() }
        everySuspend { sut.dataSource.getAppointmentHistory(businessId, 20, 0L, null) } returns firstPage

        whenn()
        val result = sut.sut.reload(businessId)

        then()
        assertEquals(firstPage, result)
    }

    @Test
    fun `loadMore fetches next page with correct offset`() = runUnitTest {
        given()
        val sut = Fixture()
        val businessId = Uuid.random()
        val firstPage = List(20) { stubAppointment() }
        val secondPage = List(10) { stubAppointment() }
        everySuspend { sut.dataSource.getAppointmentHistory(businessId, 20, 0L, null) } returns firstPage
        everySuspend { sut.dataSource.getAppointmentHistory(businessId, 20, 20L, null) } returns secondPage
        sut.sut.reload(businessId)

        whenn()
        val result = sut.sut.loadMore()

        then()
        assertEquals(secondPage, result)
    }

    @Test
    fun `loadMore returns empty when all data loaded`() = runUnitTest {
        given()
        val sut = Fixture()
        val businessId = Uuid.random()
        val partial = List(5) { stubAppointment() }
        everySuspend { sut.dataSource.getAppointmentHistory(businessId, 20, 0L, null) } returns partial
        sut.sut.reload(businessId)

        whenn()
        val result = sut.sut.loadMore()

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `loadMore throws when called before reload`() = runUnitTest {
        given()
        val sut = Fixture()

        whenn()
        then()
        assertFailsWith<CounterPager.CounterPagerException> {
            sut.sut.loadMore()
        }
    }

    @Test
    fun `reload passes query to datasource`() = runUnitTest {
        given()
        val sut = Fixture()
        val businessId = Uuid.random()
        val query = "John"
        val page = listOf(stubAppointment())
        everySuspend { sut.dataSource.getAppointmentHistory(businessId, 20, 0L, query) } returns page

        whenn()
        val result = sut.sut.reload(businessId, query)

        then()
        assertEquals(page, result)
    }
}
