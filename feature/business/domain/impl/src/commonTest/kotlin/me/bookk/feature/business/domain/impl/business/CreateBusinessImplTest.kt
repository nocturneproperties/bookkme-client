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
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.entity.Business
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
        val dataSource = mockk<BusinessDataSource>()
        val refreshBusinessInfo = mockk<RefreshBusinessInfo>()
        val sut = CreateBusinessImpl(dataSource, refreshBusinessInfo)
    }

    private fun stubBusiness() = Business(
        id = Uuid.random(),
        name = "Test Business",
        description = "",
        address = "",
        location = null,
        currency = mockk(),
        timeZone = TimeZone.UTC,
        socials = emptyMap()
    )

    @Test
    fun `returns created business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val name = "My Salon"
        val expected = stubBusiness()
        coEvery { fixture.dataSource.createBusiness(name, "UAH", any()) } returns expected
        coJustRun { fixture.refreshBusinessInfo() }

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
        coEvery { fixture.dataSource.createBusiness(name, "UAH", any()) } returns stubBusiness()
        coJustRun { fixture.refreshBusinessInfo() }

        whenn()
        fixture.sut(name)

        then()
        coVerify { fixture.dataSource.createBusiness(name, "UAH", any()) }
    }

    @Test
    fun `calls refreshBusinessInfo after creation`() = runUnitTest {
        given()
        val fixture = Fixture()
        coEvery { fixture.dataSource.createBusiness(any(), any(), any()) } returns stubBusiness()
        coJustRun { fixture.refreshBusinessInfo() }

        whenn()
        fixture.sut("My Salon")

        then()
        coVerify(exactly = 1) { fixture.refreshBusinessInfo() }
    }
}
