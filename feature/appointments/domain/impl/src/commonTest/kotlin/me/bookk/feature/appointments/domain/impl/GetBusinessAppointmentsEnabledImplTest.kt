package me.bookk.feature.appointments.domain.impl

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
import me.bookk.feature.appointments.domain.api.entity.BusinessAppointmentsEnabled
import me.bookk.feature.appointments.domain.datasource.BusinessAppointmentsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetBusinessAppointmentsEnabledImplTest {
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
        val dataSource = mock<BusinessAppointmentsDataSource>()
        val sut = GetBusinessAppointmentsEnabledImpl(dataSource)
    }

    @Test
    fun `returns the backend records including optional or disabled values`() = runUnitTest {
        given()
        val fixture = Fixture()
        val records = listOf(BusinessAppointmentsEnabled(Uuid.parse("00000000-0000-0000-0000-000000000001"), false))
        everySuspend { fixture.dataSource.getEnabled() } returns records

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(records, result)
    }

    @Test
    fun `returns an empty list when there are no records`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getEnabled() } returns emptyList()

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `propagates backend failures`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getEnabled() } throws IllegalStateException("unavailable")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }
}
