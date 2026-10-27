package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
import dev.mokkery.every
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
import library.device.api.DeviceFacade
import library.money.api.Currency
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.impl.stubBusinessPermissions
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CreateBusinessImplTest {

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
        val deviceFacade = mock<DeviceFacade> {
            every { getCurrencyCode() } returns "PLN"
        }
        val refreshBusinessInfo = mock<RefreshBusinessInfo>()
        val switchDashboardBusiness = mock<SwitchDashboardBusiness>()
        val sut = CreateBusinessImpl(dataSource, deviceFacade, refreshBusinessInfo, switchDashboardBusiness)
    }

    private fun stubBusiness() = Business(
        id = Uuid.random(),
        name = "Test Business",
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
    fun `returns created business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val name = "My Salon"
        val expected = stubBusiness()
        everySuspend { fixture.dataSource.createBusiness(name, any(), any()) } returns expected
        everySuspend { fixture.refreshBusinessInfo() } returns Unit
        everySuspend { fixture.switchDashboardBusiness(any()) } returns Unit

        whenn()
        val result = fixture.sut(name)

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `creates business in device currency when it is supported`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.deviceFacade.getCurrencyCode() } returns "PLN"
        everySuspend { fixture.dataSource.createBusiness(any(), any(), any()) } returns stubBusiness()
        everySuspend { fixture.refreshBusinessInfo() } returns Unit
        everySuspend { fixture.switchDashboardBusiness(any()) } returns Unit

        whenn()
        fixture.sut("My Salon")

        then()
        verifySuspend { fixture.dataSource.createBusiness("My Salon", "PLN", any()) }
    }

    @Test
    fun `creates business in fallback currency when device currency is not supported`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.deviceFacade.getCurrencyCode() } returns "JPY"
        everySuspend { fixture.dataSource.createBusiness(any(), any(), any()) } returns stubBusiness()
        everySuspend { fixture.refreshBusinessInfo() } returns Unit
        everySuspend { fixture.switchDashboardBusiness(any()) } returns Unit

        whenn()
        fixture.sut("My Salon")

        then()
        verifySuspend { fixture.dataSource.createBusiness("My Salon", "USD", any()) }
    }

    @Test
    fun `creates business in fallback currency when device has no currency`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.deviceFacade.getCurrencyCode() } returns null
        everySuspend { fixture.dataSource.createBusiness(any(), any(), any()) } returns stubBusiness()
        everySuspend { fixture.refreshBusinessInfo() } returns Unit
        everySuspend { fixture.switchDashboardBusiness(any()) } returns Unit

        whenn()
        fixture.sut("My Salon")

        then()
        verifySuspend { fixture.dataSource.createBusiness("My Salon", "USD", any()) }
    }

    @Test
    fun `calls refreshBusinessInfo after creation`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.createBusiness(any(), any(), any()) } returns stubBusiness()
        everySuspend { fixture.refreshBusinessInfo() } returns Unit
        everySuspend { fixture.switchDashboardBusiness(any()) } returns Unit

        whenn()
        fixture.sut("My Salon")

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.refreshBusinessInfo() }
    }

    @Test
    fun `switches dashboard business to the newly created business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val expected = stubBusiness()
        everySuspend { fixture.dataSource.createBusiness(any(), any(), any()) } returns expected
        everySuspend { fixture.refreshBusinessInfo() } returns Unit
        everySuspend { fixture.switchDashboardBusiness(any()) } returns Unit

        whenn()
        fixture.sut("My Salon")

        then()
        verifySuspend { fixture.switchDashboardBusiness(expected.id) }
    }

    @Test
    fun `sends trimmed name to backend`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.createBusiness(any(), any(), any()) } returns stubBusiness()
        everySuspend { fixture.refreshBusinessInfo() } returns Unit
        everySuspend { fixture.switchDashboardBusiness(any()) } returns Unit

        whenn()
        fixture.sut("  My Salon  ")

        then()
        verifySuspend { fixture.dataSource.createBusiness("My Salon", any(), any()) }
    }

    @Test
    fun `throws EmptyName without calling backend when name is blank`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = runCatching { fixture.sut("   ") }

        then()
        assertFailsWith<CreateBusiness.Error.EmptyName> { result.getOrThrow() }
        verifySuspend(VerifyMode.not) { fixture.dataSource.createBusiness(any(), any(), any()) }
    }
}
