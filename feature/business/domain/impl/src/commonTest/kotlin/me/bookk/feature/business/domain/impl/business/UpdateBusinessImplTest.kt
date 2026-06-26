package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
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
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateBusinessImplTest {

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
        val sut = UpdateBusinessImpl(dataSource)
    }

    private fun stubBusiness(id: Uuid = Uuid.random()) = Business(
        id = id,
        name = "Old Name",
        description = "Old Desc",
        address = "Old Address",
        location = null,
        currency = Currency("USD"),
        timeZone = TimeZone.UTC,
        socials = emptyMap()
    )

    private fun stubUpdate(id: Uuid) = Business.Update(
        id = id,
        name = "New Name",
        description = "New Desc",
        address = "New Address",
        location = null,
        currency = Currency("USD"),
        socials = emptyMap()
    )

    @Test
    fun `returns updated business with new fields`() = runUnitTest {
        given()
        val sut = Fixture()
        val id = Uuid.random()
        val current = stubBusiness(id)
        val update = stubUpdate(id)
        everySuspend { sut.dataSource.getBusinessById(id) } returns current
        everySuspend { sut.dataSource.updateBusiness(any()) } returns Unit
        everySuspend { sut.dataSource.saveBusinessInDB(any()) } returns Unit

        whenn()
        val result = sut.sut(update)

        then()
        assertEquals(update.name, result.name)
        assertEquals(update.description, result.description)
        assertEquals(update.address, result.address)
    }

    @Test
    fun `calls updateBusiness and saveBusinessInDB`() = runUnitTest {
        given()
        val sut = Fixture()
        val id = Uuid.random()
        val current = stubBusiness(id)
        val update = stubUpdate(id)
        everySuspend { sut.dataSource.getBusinessById(id) } returns current
        everySuspend { sut.dataSource.updateBusiness(any()) } returns Unit
        everySuspend { sut.dataSource.saveBusinessInDB(any()) } returns Unit

        whenn()
        sut.sut(update)

        then()
        verifySuspend { sut.dataSource.updateBusiness(any()) }
        verifySuspend { sut.dataSource.saveBusinessInDB(any()) }
    }

    @Test
    fun `throws when business not found`() = runUnitTest {
        given()
        val sut = Fixture()
        val id = Uuid.random()
        everySuspend { sut.dataSource.getBusinessById(id) } returns null

        whenn()
        val thrown = runCatching { sut.sut(stubUpdate(id)) }.exceptionOrNull()

        then()
        assertNotNull(thrown)
    }
}
