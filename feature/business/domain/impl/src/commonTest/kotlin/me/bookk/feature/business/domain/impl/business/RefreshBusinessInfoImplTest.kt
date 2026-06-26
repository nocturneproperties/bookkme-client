package me.bookk.feature.business.domain.impl.business

import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.UserBusinessInfo
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshBusinessInfoImplTest {

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
        val dataSource = mockk<BusinessDataSource>()
        val sut = RefreshBusinessInfoImpl(dataSource)
    }

    @Test
    fun `saves dashboard business id after fetching`() = runUnitTest {
        given()
        val fixture = Fixture()
        val dashboardId = Uuid.random()
        val info = UserBusinessInfo(dashboardId = dashboardId, businesses = emptyList())
        coEvery { fixture.dataSource.getBusinessesFromRemote() } returns info
        coJustRun { fixture.dataSource.saveDashboardBusinessId(dashboardId) }
        coJustRun { fixture.dataSource.saveBusinessListInDB(emptyList()) }

        whenn()
        fixture.sut()

        then()
        coVerify { fixture.dataSource.saveDashboardBusinessId(dashboardId) }
    }

    @Test
    fun `saves business list in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = Business(
            id = Uuid.random(),
            name = "B",
            description = "",
            address = "",
            location = null,
            currency = mockk(),
            timeZone = TimeZone.UTC,
            socials = emptyMap()
        )
        val info = UserBusinessInfo(dashboardId = null, businesses = listOf(business))
        coEvery { fixture.dataSource.getBusinessesFromRemote() } returns info
        coJustRun { fixture.dataSource.saveDashboardBusinessId(null) }
        coJustRun { fixture.dataSource.saveBusinessListInDB(listOf(business)) }

        whenn()
        fixture.sut()

        then()
        coVerify { fixture.dataSource.saveBusinessListInDB(listOf(business)) }
    }

    @Test
    fun `calls getBusinessesFromRemote`() = runUnitTest {
        given()
        val fixture = Fixture()
        val info = UserBusinessInfo(dashboardId = null, businesses = emptyList())
        coEvery { fixture.dataSource.getBusinessesFromRemote() } returns info
        coJustRun { fixture.dataSource.saveDashboardBusinessId(null) }
        coJustRun { fixture.dataSource.saveBusinessListInDB(emptyList()) }

        whenn()
        fixture.sut()

        then()
        coVerify(exactly = 1) { fixture.dataSource.getBusinessesFromRemote() }
    }
}
