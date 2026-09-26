package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.DashboardSetupStatus
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.impl.stubBusiness
import me.bookk.feature.business.domain.impl.stubBusinessPermissions
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveDashboardSetupStatusImplTest {

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
        val isAppointmentsPluginEnabled = mock<IsAppointmentsPluginEnabled>()
        val sut = ObserveDashboardSetupStatusImpl(observeDashboardBusinessChanges, isAppointmentsPluginEnabled)
    }

    private fun managedBusiness(): Business {
        return stubBusiness(permissions = stubBusinessPermissions().copy(business = ResourcePermission(view = true, update = true)))
    }

    private fun joinedBusiness(): Business {
        return stubBusiness(permissions = stubBusinessPermissions().copy(business = ResourcePermission(view = true)))
    }

    private fun Fixture.stub(business: Business, isAppointmentsEnabled: Boolean?) {
        every { observeDashboardBusinessChanges() } returns flowOf(business)
        every { isAppointmentsPluginEnabled.flow(business.id) } returns flowOf(isAppointmentsEnabled)
    }

    @Test
    fun `emits no business when there is no dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(null)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(DashboardSetupStatus.NoBusiness, result)
    }

    @Test
    fun `emits setup required when user can manage business and plugin is disabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = managedBusiness()
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(DashboardSetupStatus.SetupRequired(business.id), result)
    }

    @Test
    fun `emits setup required while plugin state is unknown for a manager`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = managedBusiness()
        fixture.stub(business, isAppointmentsEnabled = null)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(DashboardSetupStatus.SetupRequired(business.id), result)
    }

    @Test
    fun `emits awaiting setup when user cannot manage business and plugin is disabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = joinedBusiness()
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(DashboardSetupStatus.AwaitingSetup(business.name), result)
    }

    @Test
    fun `emits ready when plugin is enabled for a manager`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stub(managedBusiness(), isAppointmentsEnabled = true)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(DashboardSetupStatus.Ready, result)
    }

    @Test
    fun `emits ready when plugin is enabled for an employee`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stub(joinedBusiness(), isAppointmentsEnabled = true)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(DashboardSetupStatus.Ready, result)
    }

    @Test
    fun `recomputes when user joins a business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businesses = MutableSharedFlow<Business?>(replay = 1).apply { tryEmit(null) }
        val business = joinedBusiness()
        every { fixture.observeDashboardBusinessChanges() } returns businesses
        every { fixture.isAppointmentsPluginEnabled.flow(business.id) } returns flowOf(false)
        val results = mutableListOf<DashboardSetupStatus>()
        val job = launch(Dispatchers.Unconfined) { fixture.sut().collect { results.add(it) } }

        whenn()
        businesses.emit(business)

        then()
        job.cancel()
        assertEquals(listOf(DashboardSetupStatus.NoBusiness, DashboardSetupStatus.AwaitingSetup(business.name)), results)
    }

    @Test
    fun `recomputes when plugin gets enabled for the same business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = managedBusiness()
        val pluginState = MutableSharedFlow<Boolean?>(replay = 1).apply { tryEmit(false) }
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        every { fixture.isAppointmentsPluginEnabled.flow(business.id) } returns pluginState
        val results = mutableListOf<DashboardSetupStatus>()
        val job = launch(Dispatchers.Unconfined) { fixture.sut().collect { results.add(it) } }

        whenn()
        pluginState.emit(true)

        then()
        job.cancel()
        assertEquals(listOf(DashboardSetupStatus.SetupRequired(business.id), DashboardSetupStatus.Ready), results)
    }
}
