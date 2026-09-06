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
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.domain.api.entity.DashboardOverview
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.plugin.ObserveAppointmentsPluginEnabled
import me.bookk.feature.business.domain.impl.stubBusiness
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GetAvailableDashboardFeaturesImplTest {

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
        val observeAppointmentsPluginEnabled = mock<ObserveAppointmentsPluginEnabled>()
        val sut = GetAvailableDashboardFeaturesImpl(observeDashboardBusinessChanges, observeAppointmentsPluginEnabled)
    }

    private val noPermissions = BusinessPermissions(
        business = ResourcePermission(),
        employees = ResourcePermission(),
        clients = ResourcePermission(),
        services = ResourcePermission(),
        appointments = ResourcePermission()
    )

    private val fullPermissions = BusinessPermissions(
        business = ResourcePermission(view = true),
        employees = ResourcePermission(view = true),
        clients = ResourcePermission(view = true),
        services = ResourcePermission(view = true),
        appointments = ResourcePermission(view = true)
    )

    private fun Fixture.stub(business: Business, isAppointmentsEnabled: Boolean) {
        every { observeDashboardBusinessChanges() } returns flowOf(business)
        every { observeAppointmentsPluginEnabled(business.id) } returns flowOf(isAppointmentsEnabled)
    }

    private suspend fun Fixture.features(): Set<DashboardFeature> = sut().first()!!.features

    @Test
    fun `BUSINESS feature included when business view permission is granted`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness(permissions = fullPermissions)
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.features()

        then()
        assertTrue(DashboardFeature.BUSINESS in result)
    }

    @Test
    fun `BUSINESS feature excluded when business view permission is not granted`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness(permissions = noPermissions)
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.features()

        then()
        assertFalse(DashboardFeature.BUSINESS in result)
    }

    @Test
    fun `EMPLOYEES feature included only when employees view permission is granted`() = runUnitTest {
        given()
        val fixture = Fixture()
        val permissions = noPermissions.copy(employees = noPermissions.employees.copy(view = true))
        val business = stubBusiness(permissions = permissions)
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.features()

        then()
        assertEquals(setOf(DashboardFeature.EMPLOYEES), result)
    }

    @Test
    fun `CLIENTS feature included only when clients view permission is granted`() = runUnitTest {
        given()
        val fixture = Fixture()
        val permissions = noPermissions.copy(clients = noPermissions.clients.copy(view = true))
        val business = stubBusiness(permissions = permissions)
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.features()

        then()
        assertEquals(setOf(DashboardFeature.CLIENTS), result)
    }

    @Test
    fun `SERVICES feature included only when services view permission is granted`() = runUnitTest {
        given()
        val fixture = Fixture()
        val permissions = noPermissions.copy(services = noPermissions.services.copy(view = true))
        val business = stubBusiness(permissions = permissions)
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.features()

        then()
        assertEquals(setOf(DashboardFeature.SERVICES), result)
    }

    @Test
    fun `APPOINTMENTS feature included when plugin is enabled and view permission is granted`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness(permissions = fullPermissions)
        fixture.stub(business, isAppointmentsEnabled = true)

        whenn()
        val result = fixture.features()

        then()
        assertTrue(DashboardFeature.APPOINTMENTS in result)
    }

    @Test
    fun `APPOINTMENTS feature not included when plugin is disabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness(permissions = fullPermissions)
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.features()

        then()
        assertFalse(DashboardFeature.APPOINTMENTS in result)
    }

    @Test
    fun `APPOINTMENTS feature not included when view permission is not granted`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness(permissions = noPermissions)
        fixture.stub(business, isAppointmentsEnabled = true)

        whenn()
        val result = fixture.features()

        then()
        assertFalse(DashboardFeature.APPOINTMENTS in result)
    }

    @Test
    fun `includes the business in the emitted overview`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness(permissions = fullPermissions)
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(business, result?.business)
    }

    @Test
    fun `emits null when there is no dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(null)

        whenn()
        val result = fixture.sut().first()

        then()
        assertNull(result)
    }

    @Test
    fun `recomputes when the dashboard business changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businesses = MutableSharedFlow<Business?>(replay = 1)
        val firstBusiness = stubBusiness(permissions = noPermissions)
        val secondBusiness = stubBusiness(permissions = fullPermissions)
        businesses.tryEmit(firstBusiness)
        every { fixture.observeDashboardBusinessChanges() } returns businesses
        every { fixture.observeAppointmentsPluginEnabled(firstBusiness.id) } returns flowOf(false)
        every { fixture.observeAppointmentsPluginEnabled(secondBusiness.id) } returns flowOf(false)
        val results = mutableListOf<DashboardOverview?>()
        val job = launch(Dispatchers.Unconfined) {
            fixture.sut().collect { results.add(it) }
        }

        whenn()
        businesses.emit(secondBusiness)

        then()
        job.cancel()
        assertEquals(secondBusiness.id, results.last()?.business?.id)
        assertFalse(DashboardFeature.BUSINESS in results.first()!!.features)
        assertTrue(DashboardFeature.BUSINESS in results.last()!!.features)
    }

    @Test
    fun `recomputes when the plugin state changes for the same business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness(permissions = fullPermissions)
        val pluginState = MutableSharedFlow<Boolean>(replay = 1).apply { tryEmit(false) }
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        every { fixture.observeAppointmentsPluginEnabled(business.id) } returns pluginState
        val results = mutableListOf<DashboardOverview?>()
        val job = launch(Dispatchers.Unconfined) {
            fixture.sut().collect { results.add(it) }
        }

        whenn()
        pluginState.emit(true)

        then()
        job.cancel()
        assertFalse(DashboardFeature.APPOINTMENTS in results.first()!!.features)
        assertTrue(DashboardFeature.APPOINTMENTS in results.last()!!.features)
    }
}
