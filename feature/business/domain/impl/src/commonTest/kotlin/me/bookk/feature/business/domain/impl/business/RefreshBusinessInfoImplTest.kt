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
        val dataSource = mock<BusinessDataSource> {
            everySuspend { getBusinessIdsInDb() } returns emptyList()
        }
        val isAppointmentsPluginEnabled = mock<IsAppointmentsPluginEnabled>()
        val sut = RefreshBusinessInfoImpl(dataSource, isAppointmentsPluginEnabled)
    }

    @Test
    fun `saves businesses together with the remote dashboard business when applyDashboardIdFromRemote is true`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        val info = UserBusinessInfo(dashboardId = business.id, businesses = listOf(business))
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns info
        everySuspend { fixture.dataSource.saveBusinessListWithDashboardInDB(any(), any()) } returns Unit
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(any()) } returns true

        whenn()
        fixture.sut(applyDashboardIdFromRemote = true)

        then()
        verifySuspend { fixture.dataSource.saveBusinessListWithDashboardInDB(listOf(business), business.id) }
    }

    @Test
    fun `clears the dashboard business when the remote one is not among the user businesses`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        val info = UserBusinessInfo(dashboardId = Uuid.random(), businesses = listOf(business))
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns info
        everySuspend { fixture.dataSource.saveBusinessListWithDashboardInDB(any(), any()) } returns Unit
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(any()) } returns true

        whenn()
        fixture.sut(applyDashboardIdFromRemote = true)

        then()
        verifySuspend { fixture.dataSource.saveBusinessListWithDashboardInDB(listOf(business), null) }
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
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveBusinessListWithDashboardInDB(any(), any()) }
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
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(business.id) } returns true

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
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(businessA.id) } returns true
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(businessB.id) } returns false

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.isAppointmentsPluginEnabled.refresh(businessA.id) }
        verifySuspend { fixture.isAppointmentsPluginEnabled.refresh(businessB.id) }
    }

    @Test
    fun `does not throw when refreshing plugin state fails for a business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        val info = UserBusinessInfo(dashboardId = null, businesses = listOf(business))
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns info
        everySuspend { fixture.dataSource.saveBusinessListInDB(any()) } returns Unit
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(business.id) } throws RuntimeException("network down")

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.dataSource.saveBusinessListInDB(listOf(business)) }
    }

    @Test
    fun `deletes local businesses missing from the remote response`() = runUnitTest {
        given()
        val fixture = Fixture()
        val kept = stubBusiness()
        val removedA = Uuid.random()
        val removedB = Uuid.random()
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns UserBusinessInfo(dashboardId = null, businesses = listOf(kept))
        everySuspend { fixture.dataSource.getBusinessIdsInDb() } returns listOf(removedA, kept.id, removedB)
        everySuspend { fixture.dataSource.deleteBusinessesInDb(any()) } returns Unit
        everySuspend { fixture.dataSource.saveBusinessListInDB(any()) } returns Unit
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(any()) } returns true

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteBusinessesInDb(listOf(removedA, removedB)) }
    }

    @Test
    fun `deletes every local business when the remote response is empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        val removed = Uuid.random()
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns UserBusinessInfo(dashboardId = null, businesses = emptyList())
        everySuspend { fixture.dataSource.getBusinessIdsInDb() } returns listOf(removed)
        everySuspend { fixture.dataSource.deleteBusinessesInDb(any()) } returns Unit
        everySuspend { fixture.dataSource.saveBusinessListInDB(any()) } returns Unit

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteBusinessesInDb(listOf(removed)) }
    }

    @Test
    fun `does not delete anything when every local business is still remote`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns UserBusinessInfo(dashboardId = null, businesses = listOf(business))
        everySuspend { fixture.dataSource.getBusinessIdsInDb() } returns listOf(business.id)
        everySuspend { fixture.dataSource.saveBusinessListInDB(any()) } returns Unit
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(any()) } returns true

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.not) { fixture.dataSource.deleteBusinessesInDb(any()) }
    }

    @Test
    fun `does not delete local businesses when the remote fetch fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } throws RuntimeException("network down")
        everySuspend { fixture.dataSource.getBusinessIdsInDb() } returns listOf(Uuid.random())

        whenn()
        runCatching { fixture.sut() }

        then()
        verifySuspend(VerifyMode.not) { fixture.dataSource.deleteBusinessesInDb(any()) }
    }

    @Test
    fun `saves businesses together with the selected business on refresh and select`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns UserBusinessInfo(dashboardId = null, businesses = listOf(business))
        everySuspend { fixture.dataSource.saveBusinessListWithDashboardInDB(any(), any()) } returns Unit
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(any()) } returns true

        whenn()
        fixture.sut.refreshAndSelect(business.id)

        then()
        verifySuspend { fixture.dataSource.saveBusinessListWithDashboardInDB(listOf(business), business.id) }
    }

    @Test
    fun `keeps the local dashboard business when the selected one is missing from the response`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns UserBusinessInfo(dashboardId = null, businesses = listOf(business))
        everySuspend { fixture.dataSource.saveBusinessListInDB(any()) } returns Unit
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(any()) } returns true

        whenn()
        fixture.sut.refreshAndSelect(Uuid.random())

        then()
        verifySuspend { fixture.dataSource.saveBusinessListInDB(listOf(business)) }
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveBusinessListWithDashboardInDB(any(), any()) }
    }

    @Test
    fun `deletes local businesses missing from the remote response on refresh and select`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        val removed = Uuid.random()
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns UserBusinessInfo(dashboardId = null, businesses = listOf(business))
        everySuspend { fixture.dataSource.getBusinessIdsInDb() } returns listOf(removed)
        everySuspend { fixture.dataSource.deleteBusinessesInDb(any()) } returns Unit
        everySuspend { fixture.dataSource.saveBusinessListWithDashboardInDB(any(), any()) } returns Unit
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(any()) } returns true

        whenn()
        fixture.sut.refreshAndSelect(business.id)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteBusinessesInDb(listOf(removed)) }
    }

    @Test
    fun `refreshes appointments plugin state for every business on refresh and select`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        everySuspend { fixture.dataSource.getBusinessesFromRemote() } returns UserBusinessInfo(dashboardId = null, businesses = listOf(business))
        everySuspend { fixture.dataSource.saveBusinessListWithDashboardInDB(any(), any()) } returns Unit
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(any()) } returns true

        whenn()
        fixture.sut.refreshAndSelect(business.id)

        then()
        verifySuspend { fixture.isAppointmentsPluginEnabled.refresh(business.id) }
    }
}
