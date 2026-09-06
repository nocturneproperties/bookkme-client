package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
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
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.impl.stubBusiness
import me.bookk.feature.business.domain.impl.stubBusinessPermissions
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
        val isAppointmentsPluginEnabled = mock<IsAppointmentsPluginEnabled>()
        val sut = RefreshBusinessInfoImpl(dataSource, isAppointmentsPluginEnabled)
    }

    @Test
    fun `saves dashboard business id when applyDashboardIdFromRemote is true`() = runUnitTest {
        given()
        val fixture = Fixture()
        val dashboardId = Uuid.random()
        val info = UserBusinessInfo(dashboardId = dashboardId, businesses = emptyList())
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns info
        everySuspend { fixture.dataSource.saveDashboardBusinessId(dashboardId) } returns Unit
        everySuspend { fixture.dataSource.saveBusinessListInDB(emptyList()) } returns Unit

        whenn()
        fixture.sut(applyDashboardIdFromRemote = true)

        then()
        verifySuspend { fixture.dataSource.saveDashboardBusinessId(dashboardId) }
    }

    @Test
    fun `does not save dashboard business id when applyDashboardIdFromRemote is false`() = runUnitTest {
        given()
        val fixture = Fixture()
        val info = UserBusinessInfo(dashboardId = Uuid.random(), businesses = emptyList())
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns info
        everySuspend { fixture.dataSource.saveBusinessListInDB(emptyList()) } returns Unit

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveDashboardBusinessId(any()) }
    }

    @Test
    fun `saves business list in DB regardless of applyDashboardIdFromRemote`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = Business(
            id = Uuid.random(),
            name = "B",
            description = "",
            address = "",
            location = null,
            currency = Currency("USD"),
            timeZone = TimeZone.UTC,
            socials = emptyMap(),
            schedule = WorkingSchedule(),
            permissions = stubBusinessPermissions()
        )
        val info = UserBusinessInfo(dashboardId = null, businesses = listOf(business))
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns info
        everySuspend { fixture.dataSource.saveBusinessListInDB(listOf(business)) } returns Unit
        everySuspend { fixture.isAppointmentsPluginEnabled(business.id) } returns true

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.dataSource.saveBusinessListInDB(listOf(business)) }
    }

    @Test
    fun `calls getBusinessesFromRemote`() = runUnitTest {
        given()
        val fixture = Fixture()
        val info = UserBusinessInfo(dashboardId = null, businesses = emptyList())
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns info
        everySuspend { fixture.dataSource.saveBusinessListInDB(emptyList()) } returns Unit

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.getBusinessesFromRemote() }
    }

    @Test
    fun `refreshes appointments plugin state for every business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessA = stubBusiness()
        val businessB = stubBusiness()
        val info = UserBusinessInfo(dashboardId = null, businesses = listOf(businessA, businessB))
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns info
        everySuspend { fixture.dataSource.saveBusinessListInDB(any()) } returns Unit
        everySuspend { fixture.isAppointmentsPluginEnabled(businessA.id) } returns true
        everySuspend { fixture.isAppointmentsPluginEnabled(businessB.id) } returns false

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.isAppointmentsPluginEnabled(businessA.id) }
        verifySuspend { fixture.isAppointmentsPluginEnabled(businessB.id) }
    }

    @Test
    fun `does not throw when refreshing plugin state fails for a business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        val info = UserBusinessInfo(dashboardId = null, businesses = listOf(business))
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns info
        everySuspend { fixture.dataSource.saveBusinessListInDB(any()) } returns Unit
        everySuspend { fixture.isAppointmentsPluginEnabled(business.id) } throws RuntimeException("network down")

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.dataSource.saveBusinessListInDB(listOf(business)) }
    }
}
