package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.core.domain.entity.Error
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
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetBusinessByIdImplTest {

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
        val sut = GetBusinessByIdImpl(dataSource)
    }

    private fun stubBusiness(id: Uuid = Uuid.random()) = Business(
        id = id,
        name = "Test Business",
        description = "",
        address = "",
        location = null,
        currency = Currency("USD"),
        timeZone = TimeZone.UTC,
        socials = emptyMap()
    )

    @Test
    fun `returns business when found`() = runUnitTest {
        given()
        val sut = Fixture()
        val id = Uuid.random()
        val expected = stubBusiness(id)
        everySuspend { sut.dataSource.getBusinessById(id) } returns expected

        whenn()
        val result = sut.sut(id)

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `throws when datasource returns null`() = runUnitTest {
        given()
        val sut = Fixture()
        val id = Uuid.random()
        everySuspend { sut.dataSource.getBusinessById(id) } returns null

        whenn()
        val thrown = runCatching { sut.sut(id) }.exceptionOrNull()

        then()
        assertNotNull(thrown)
        assertTrue(thrown is Error.InvalidApplicationState)
    }
}
