package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
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
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
        val refreshBusinessInfo = mock<RefreshBusinessInfo>()
        val sut = CreateBusinessImpl(dataSource, refreshBusinessInfo)
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
        schedule = WorkingSchedule()
    )

    @Test
    fun `returns created business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val name = "My Salon"
        val expected = stubBusiness()
        everySuspend { fixture.dataSource.createBusiness(name, "UAH", any()) } returns expected
        everySuspend { fixture.refreshBusinessInfo() } returns Unit

        whenn()
        val result = fixture.sut(name)

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `calls createBusiness with UAH currency`() = runUnitTest {
        given()
        val fixture = Fixture()
        val name = "My Salon"
        everySuspend { fixture.dataSource.createBusiness(name, "UAH", any()) } returns stubBusiness()
        everySuspend { fixture.refreshBusinessInfo() } returns Unit

        whenn()
        fixture.sut(name)

        then()
        verifySuspend { fixture.dataSource.createBusiness(name, "UAH", any()) }
    }

    @Test
    fun `calls refreshBusinessInfo after creation`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.createBusiness(any(), any(), any()) } returns stubBusiness()
        everySuspend { fixture.refreshBusinessInfo() } returns Unit

        whenn()
        fixture.sut("My Salon")

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.refreshBusinessInfo() }
    }
}
