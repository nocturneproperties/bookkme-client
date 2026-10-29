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
import me.bookk.feature.business.domain.api.entity.BusinessResource
import me.bookk.feature.business.domain.api.entity.DashboardSetupStatus
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.impl.stubBusiness
import me.bookk.feature.business.domain.impl.stubBusinessPermissions
import me.bookk.feature.business.domain.impl.stubClient
import me.bookk.feature.business.domain.impl.stubService
import me.bookk.feature.business.domain.impl.stubServiceGroup
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.domain.api.service.entity.Service
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
        val getServiceGroups = mock<GetServiceGroups>()
        val getServices = mock<GetServices>()
        val getClientsList = mock<GetClientsList>()
        val sut = ObserveDashboardSetupStatusImpl(
            observeDashboardBusinessChanges,
            isAppointmentsPluginEnabled,
            getServiceGroups,
            getServices,
            getClientsList
        )
    }

    private fun businessWith(
        canEditBusiness: Boolean = false,
        canEditServices: Boolean = false,
        canEditClients: Boolean = false,
        canViewServices: Boolean = true,
        canViewClients: Boolean = true
    ): Business {
        return stubBusiness(
            permissions = stubBusinessPermissions().copy(
                business = ResourcePermission(view = true, update = canEditBusiness),
                services = ResourcePermission(view = canViewServices, update = canEditServices),
                clients = ResourcePermission(view = canViewClients, update = canEditClients)
            )
        )
    }

    private fun ownedBusiness(): Business {
        return businessWith(canEditBusiness = true, canEditServices = true, canEditClients = true)
    }

    private fun Fixture.stub(
        business: Business,
        isAppointmentsEnabled: Boolean?,
        groups: List<ServiceGroup> = emptyList(),
        services: List<Service> = emptyList(),
        clients: List<Client> = emptyList()
    ) {
        every { observeDashboardBusinessChanges() } returns flowOf(business)
        every { isAppointmentsPluginEnabled.flow(business.id) } returns flowOf(isAppointmentsEnabled)
        every { getServiceGroups.flow() } returns flowOf(groups)
        every { getServices.flow() } returns flowOf(services)
        every { getClientsList.flow() } returns flowOf(clients)
    }

    private fun setupRequired(
        business: Business,
        isAppointmentsEnabled: Boolean = false,
        hasServiceGroups: Boolean = false,
        hasServices: Boolean = false,
        hasClients: Boolean = false,
        canManageServices: Boolean = business.permissions.services.update,
        canManageClients: Boolean = business.permissions.clients.update,
        missingViewPermissions: Set<BusinessResource> = emptySet()
    ): DashboardSetupStatus.SetupRequired {
        return DashboardSetupStatus.SetupRequired(
            businessId = business.id,
            isAppointmentsEnabled = isAppointmentsEnabled,
            hasServiceGroups = hasServiceGroups,
            hasServices = hasServices,
            hasClients = hasClients,
            canEnableAppointments = business.permissions.business.update,
            canManageServices = canManageServices,
            canManageClients = canManageClients,
            missingViewPermissions = missingViewPermissions
        )
    }

    private fun awaitingSetup(
        business: Business,
        missingViewPermissions: Set<BusinessResource> = emptySet()
    ): DashboardSetupStatus.AwaitingSetup {
        return DashboardSetupStatus.AwaitingSetup(business.id, business.name, missingViewPermissions)
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
    fun `emits setup required with no steps done for a new owned business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = ownedBusiness()
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(setupRequired(business), result)
    }

    @Test
    fun `treats unknown plugin state as not enabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = ownedBusiness()
        fixture.stub(
            business,
            isAppointmentsEnabled = null,
            groups = listOf(stubServiceGroup()),
            services = listOf(stubService()),
            clients = listOf(stubClient())
        )

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(setupRequired(business, hasServiceGroups = true, hasServices = true, hasClients = true), result)
    }

    @Test
    fun `marks plugin step done when appointments are enabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = ownedBusiness()
        fixture.stub(business, isAppointmentsEnabled = true)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(setupRequired(business, isAppointmentsEnabled = true), result)
    }

    @Test
    fun `marks service group step done when business has a group`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = ownedBusiness()
        fixture.stub(business, isAppointmentsEnabled = true, groups = listOf(stubServiceGroup()))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(setupRequired(business, isAppointmentsEnabled = true, hasServiceGroups = true), result)
    }

    @Test
    fun `marks service step done when business has a service`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = ownedBusiness()
        fixture.stub(business, isAppointmentsEnabled = true, groups = listOf(stubServiceGroup()), services = listOf(stubService()))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(setupRequired(business, isAppointmentsEnabled = true, hasServiceGroups = true, hasServices = true), result)
    }

    @Test
    fun `marks client step done when business has a client`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = ownedBusiness()
        fixture.stub(business, isAppointmentsEnabled = true, clients = listOf(stubClient()))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(setupRequired(business, isAppointmentsEnabled = true, hasClients = true), result)
    }

    @Test
    fun `emits ready when every step is done`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stub(
            ownedBusiness(),
            isAppointmentsEnabled = true,
            groups = listOf(stubServiceGroup()),
            services = listOf(stubService()),
            clients = listOf(stubClient())
        )

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(DashboardSetupStatus.Ready, result)
    }

    @Test
    fun `emits ready for a user without edit permissions once every step is done`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stub(
            businessWith(),
            isAppointmentsEnabled = true,
            groups = listOf(stubServiceGroup()),
            services = listOf(stubService()),
            clients = listOf(stubClient())
        )

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(DashboardSetupStatus.Ready, result)
    }

    @Test
    fun `emits awaiting setup when user cannot edit anything and setup is incomplete`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith()
        fixture.stub(business, isAppointmentsEnabled = true)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(awaitingSetup(business), result)
    }

    @Test
    fun `emits setup required for a user who can only edit clients while client step is missing`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canEditClients = true)
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(setupRequired(business), result)
    }

    @Test
    fun `emits awaiting setup for a user who can only edit clients once a client exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canEditClients = true)
        fixture.stub(business, isAppointmentsEnabled = false, clients = listOf(stubClient()))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(awaitingSetup(business), result)
    }

    @Test
    fun `emits setup required for a user who can only edit services while service group step is missing`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canEditServices = true)
        fixture.stub(business, isAppointmentsEnabled = true, clients = listOf(stubClient()))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(setupRequired(business, isAppointmentsEnabled = true, hasClients = true), result)
    }

    @Test
    fun `emits setup required for a user who can only edit services while service step is missing`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canEditServices = true)
        fixture.stub(business, isAppointmentsEnabled = true, groups = listOf(stubServiceGroup()), clients = listOf(stubClient()))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(setupRequired(business, isAppointmentsEnabled = true, hasServiceGroups = true, hasClients = true), result)
    }

    @Test
    fun `emits awaiting setup for a user who can only edit services once groups and services exist`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canEditServices = true)
        fixture.stub(business, isAppointmentsEnabled = false, groups = listOf(stubServiceGroup()), services = listOf(stubService()))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(awaitingSetup(business), result)
    }

    @Test
    fun `emits setup required for a user who can only edit business while plugin is disabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canEditBusiness = true)
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(setupRequired(business), result)
    }

    @Test
    fun `emits awaiting setup for a user who can only edit business once plugin is enabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canEditBusiness = true)
        fixture.stub(business, isAppointmentsEnabled = true)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(awaitingSetup(business), result)
    }

    @Test
    fun `reports missing clients view permission while awaiting setup`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canViewClients = false)
        fixture.stub(business, isAppointmentsEnabled = true, groups = listOf(stubServiceGroup()), services = listOf(stubService()))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(awaitingSetup(business, setOf(BusinessResource.CLIENTS)), result)
    }

    @Test
    fun `reports missing services view permission while awaiting setup`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canViewServices = false)
        fixture.stub(business, isAppointmentsEnabled = true, clients = listOf(stubClient()))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(awaitingSetup(business, setOf(BusinessResource.SERVICES)), result)
    }

    @Test
    fun `reports both missing view permissions while awaiting setup`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canViewServices = false, canViewClients = false)
        fixture.stub(business, isAppointmentsEnabled = true)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(awaitingSetup(business, setOf(BusinessResource.SERVICES, BusinessResource.CLIENTS)), result)
    }

    @Test
    fun `reports missing services view permission on the checklist of a user who can edit the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canEditBusiness = true, canViewServices = false)
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(setupRequired(business, missingViewPermissions = setOf(BusinessResource.SERVICES)), result)
    }

    @Test
    fun `reports missing clients view permission on the checklist of a user who can edit the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canEditBusiness = true, canViewClients = false)
        fixture.stub(business, isAppointmentsEnabled = false)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(setupRequired(business, missingViewPermissions = setOf(BusinessResource.CLIENTS)), result)
    }

    @Test
    fun `does not let a user manage services they cannot view`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canEditServices = true, canViewServices = false)
        fixture.stub(business, isAppointmentsEnabled = true, clients = listOf(stubClient()))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(awaitingSetup(business, setOf(BusinessResource.SERVICES)), result)
    }

    @Test
    fun `does not let a user manage clients they cannot view`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = businessWith(canEditClients = true, canViewClients = false)
        fixture.stub(business, isAppointmentsEnabled = true, groups = listOf(stubServiceGroup()), services = listOf(stubService()))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(awaitingSetup(business, setOf(BusinessResource.CLIENTS)), result)
    }

    @Test
    fun `recomputes when user joins a business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businesses = MutableSharedFlow<Business?>(replay = 1).apply { tryEmit(null) }
        val business = businessWith()
        fixture.stub(business, isAppointmentsEnabled = false)
        every { fixture.observeDashboardBusinessChanges() } returns businesses
        val results = mutableListOf<DashboardSetupStatus>()
        val job = launch(Dispatchers.Unconfined) { fixture.sut().collect { results.add(it) } }

        whenn()
        businesses.emit(business)

        then()
        job.cancel()
        assertEquals(listOf(DashboardSetupStatus.NoBusiness, awaitingSetup(business)), results)
    }

    @Test
    fun `recomputes when plugin gets enabled for the same business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = ownedBusiness()
        val pluginState = MutableSharedFlow<Boolean?>(replay = 1).apply { tryEmit(false) }
        fixture.stub(
            business,
            isAppointmentsEnabled = false,
            groups = listOf(stubServiceGroup()),
            services = listOf(stubService()),
            clients = listOf(stubClient())
        )
        every { fixture.isAppointmentsPluginEnabled.flow(business.id) } returns pluginState
        val results = mutableListOf<DashboardSetupStatus>()
        val job = launch(Dispatchers.Unconfined) { fixture.sut().collect { results.add(it) } }

        whenn()
        pluginState.emit(true)

        then()
        job.cancel()
        assertEquals(
            listOf(setupRequired(business, hasServiceGroups = true, hasServices = true, hasClients = true), DashboardSetupStatus.Ready),
            results
        )
    }

    @Test
    fun `emits ready once the last missing client is created`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = ownedBusiness()
        val clients = MutableSharedFlow<List<Client>>(replay = 1).apply { tryEmit(emptyList()) }
        fixture.stub(business, isAppointmentsEnabled = true, groups = listOf(stubServiceGroup()), services = listOf(stubService()))
        every { fixture.getClientsList.flow() } returns clients
        val results = mutableListOf<DashboardSetupStatus>()
        val job = launch(Dispatchers.Unconfined) { fixture.sut().collect { results.add(it) } }

        whenn()
        clients.emit(listOf(stubClient(business.id)))

        then()
        job.cancel()
        assertEquals(
            listOf(setupRequired(business, isAppointmentsEnabled = true, hasServiceGroups = true, hasServices = true), DashboardSetupStatus.Ready),
            results
        )
    }
}
