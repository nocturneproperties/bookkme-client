package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.domain.fetch.BusinessDataFetchAction
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.impl.stubBusiness
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardBusinessDataFetchTest {

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
        val observeDashboardBusinessChanges = mock<ObserveDashboardBusinessChanges>()
        val first = mock<BusinessDataFetchAction>()
        val second = mock<BusinessDataFetchAction>()
        val sut = DashboardBusinessDataFetch(observeDashboardBusinessChanges, listOf(first, second))
    }

    @Test
    fun `runs every business data fetch for the dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        everySuspend { fixture.first.doOnBusinessDataFetch(business.id) } returns Unit
        everySuspend { fixture.second.doOnBusinessDataFetch(business.id) } returns Unit

        whenn()
        fixture.sut.doOnBackgroundDataFetch()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.first.doOnBusinessDataFetch(business.id) }
        verifySuspend(VerifyMode.exactly(1)) { fixture.second.doOnBusinessDataFetch(business.id) }
    }

    @Test
    fun `skips the business data fetch when there is no dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(null)

        whenn()
        fixture.sut.doOnBackgroundDataFetch()

        then()
        verifySuspend(VerifyMode.not) { fixture.first.doOnBusinessDataFetch(any()) }
        verifySuspend(VerifyMode.not) { fixture.second.doOnBusinessDataFetch(any()) }
    }

    @Test
    fun `uses the current dashboard business without waiting for later changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val later = stubBusiness()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(null, later)

        whenn()
        fixture.sut.doOnBackgroundDataFetch()

        then()
        verifySuspend(VerifyMode.not) { fixture.first.doOnBusinessDataFetch(any()) }
    }

    @Test
    fun `still runs the remaining fetches when one fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        everySuspend { fixture.first.doOnBusinessDataFetch(business.id) } throws
            IllegalStateException()
        everySuspend { fixture.second.doOnBusinessDataFetch(business.id) } returns Unit

        whenn()
        fixture.sut.doOnBackgroundDataFetch()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.second.doOnBusinessDataFetch(business.id) }
    }
}
