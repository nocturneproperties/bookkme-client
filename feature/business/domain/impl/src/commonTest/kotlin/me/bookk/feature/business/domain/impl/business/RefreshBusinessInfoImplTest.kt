package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        val dataSource = mock<BusinessDataSource>()
        val sut = RefreshBusinessInfoImpl(dataSource)
    }

    @Test
    fun `saves dashboard business id after fetching`() = runUnitTest {
        given()
        val sut = Fixture()
        val dashboardId = Uuid.random()
        val info = UserBusinessInfo(dashboardId = dashboardId, businesses = emptyList())
        everySuspend { sut.dataSource.getBusinessesFromRemote() } returns info
        everySuspend { sut.dataSource.saveDashboardBusinessId(dashboardId) } returns Unit
        everySuspend { sut.dataSource.saveBusinessListInDB(emptyList()) } returns Unit

        whenn()
        sut.sut()

        then()
        verifySuspend { sut.dataSource.saveDashboardBusinessId(dashboardId) }
    }

    @Test
    fun `saves business list in DB`() = runUnitTest {
        given()
        val sut = Fixture()
        val business = Business(
            id = Uuid.random(),
            name = "B",
            description = "",
            address = "",
            location = null,
            currency = Currency("USD"),
            timeZone = TimeZone.UTC,
            socials = emptyMap()
        )
        val info = UserBusinessInfo(dashboardId = null, businesses = listOf(business))
        everySuspend { sut.dataSource.getBusinessesFromRemote() } returns info
        everySuspend { sut.dataSource.saveDashboardBusinessId(null) } returns Unit
        everySuspend { sut.dataSource.saveBusinessListInDB(listOf(business)) } returns Unit

        whenn()
        sut.sut()

        then()
        verifySuspend { sut.dataSource.saveBusinessListInDB(listOf(business)) }
    }

    @Test
    fun `calls getBusinessesFromRemote`() = runUnitTest {
        given()
        val sut = Fixture()
        val info = UserBusinessInfo(dashboardId = null, businesses = emptyList())
        everySuspend { sut.dataSource.getBusinessesFromRemote() } returns info
        everySuspend { sut.dataSource.saveDashboardBusinessId(null) } returns Unit
        everySuspend { sut.dataSource.saveBusinessListInDB(emptyList()) } returns Unit

        whenn()
        sut.sut()

        then()
        verifySuspend(VerifyMode.exactly(1)) { sut.dataSource.getBusinessesFromRemote() }
    }
}
