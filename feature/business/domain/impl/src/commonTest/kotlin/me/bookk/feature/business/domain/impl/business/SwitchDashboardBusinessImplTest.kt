package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class SwitchDashboardBusinessImplTest {

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
        val sut = SwitchDashboardBusinessImpl(dataSource)
    }

    @Test
    fun `saves the business id locally`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.saveDashboardBusinessId(businessId) } returns Unit
        everySuspend { fixture.dataSource.setDashboardBusinessOnRemote(businessId) } returns Unit

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend { fixture.dataSource.saveDashboardBusinessId(businessId) }
    }

    @Test
    fun `syncs the business id to remote`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.saveDashboardBusinessId(businessId) } returns Unit
        everySuspend { fixture.dataSource.setDashboardBusinessOnRemote(businessId) } returns Unit

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend { fixture.dataSource.setDashboardBusinessOnRemote(businessId) }
    }

    @Test
    fun `does not throw when remote sync fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.saveDashboardBusinessId(businessId) } returns Unit
        everySuspend { fixture.dataSource.setDashboardBusinessOnRemote(businessId) } throws RuntimeException("network down")

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend { fixture.dataSource.saveDashboardBusinessId(businessId) }
    }
}
