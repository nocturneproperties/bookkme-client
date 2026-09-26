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
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.impl.stubBusiness
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveUserBusinessesChangesImplTest {

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
        val sut = ObserveUserBusinessesChangesImpl(dataSource)
    }

    @Test
    fun `emits businesses cached in db`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businesses = listOf(stubBusiness(), stubBusiness())
        every { fixture.dataSource.observeAllBusinessesInDb() } returns flowOf(businesses)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(businesses, result)
    }

    @Test
    fun `emits empty list when no businesses are cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.dataSource.observeAllBusinessesInDb() } returns flowOf(emptyList())

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(emptyList(), result)
    }
}
