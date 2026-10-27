package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.impl.stubBusinessPermissions
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveDashboardBusinessChangesImplTest {

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
        val dataSource = mock<BusinessDataSource>()
        val sut = ObserveDashboardBusinessChangesImpl(dataSource)
    }

    private fun stubBusiness(id: Uuid = Uuid.random()) = Business(
        id = id,
        name = "Business",
        description = "",
        address = "",
        location = null,
        currency = Currency("USD"),
        timeZone = TimeZone.UTC,
        socials = emptyMap(),
        schedule = WorkingSchedule(),
        permissions = stubBusinessPermissions()
    )

    @Test
    fun `emits business when dashboard id exists and business is found`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val business = stubBusiness(id = businessId)
        every { fixture.dataSource.getDashboardBusinessIdFlow() } returns flowOf(businessId)
        every { fixture.dataSource.observeBusinessDBChanges(businessId) } returns flowOf(business)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(business, result)
    }

    @Test
    fun `emits null when dashboard id flow emits null`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.dataSource.getDashboardBusinessIdFlow() } returns flowOf(null)

        whenn()
        val result = fixture.sut().first()

        then()
        assertNull(result)
    }
}
