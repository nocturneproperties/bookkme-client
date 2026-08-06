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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveDashboardBusinessIdChangesImplTest {

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
        val sut = ObserveDashboardBusinessIdChangesImpl(dataSource)
    }

    @Test
    fun `emits dashboard business id when data source has one cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        every { fixture.dataSource.getDashboardBusinessIdFlow() } returns flowOf(businessId)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(businessId, result)
    }

    @Test
    fun `emits null when no dashboard business id is cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.dataSource.getDashboardBusinessIdFlow() } returns flowOf(null)

        whenn()
        val result = fixture.sut().first()

        then()
        assertNull(result)
    }
}
