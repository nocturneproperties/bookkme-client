package me.bookk.feature.dashboard.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import me.bookk.android.feature.dashboard.resources.DashboardRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.designsystem.test.FakeRefreshState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.domain.api.business.JoinBusiness
import me.bookk.feature.business.domain.api.business.ObserveDashboardSetupStatus
import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness
import me.bookk.feature.business.domain.api.entity.BusinessResource
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.business.RefreshDashboardSetupStatus
import me.bookk.feature.business.domain.api.entity.DashboardSetupStatus
import me.bookk.feature.dashboard.presentation.state.DashboardHomeState
import me.bookk.feature.dashboard.presentation.state.DashboardState
import me.bookk.feature.dashboard.presentation.state.HomeContent
import me.bookk.feature.dashboard.presentation.state.OnboardingBusinessItem
import me.bookk.feature.dashboard.presentation.state.OnboardingState
import me.bookk.feature.dashboard.presentation.state.TabItem
import me.bookk.feature.dashboard.presentation.state.TabItemsState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class DashboardViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class FakeTabItem(
        override val id: TabItem.Id,
        override val text: StringDesc,
        override var badgeText: StringDesc?,
        override var isEnabled: Boolean
    ) : TabItem

    private class FakeDashboardState(initData: TabItemsState.InitData) : DashboardState {
        override val tabItems = object : TabItemsState {
            override var selectedItemId: TabItem.Id = initData.selectedItemId
            override val items: List<TabItem> = initData.tabInitData.map {
                FakeTabItem(it.id, it.text, it.badgeText, it.isEnabled)
            }
        }
        override val navigation = FakeNavigationState<DashboardHomeNavigationDestination>()
        override val home = object : DashboardHomeState {
            override var content: HomeContent? = null
            override val onboarding = object : OnboardingState {
                override var awaitingSetupMessage: StringDesc = "".desc()
                override var onCreateBusinessClick: (() -> Unit)? = null
                override var onJoinBusinessClick: (() -> Unit)? = null
                override var onEnablePluginsClick: (() -> Unit)? = null
                override var onAddServiceGroupClick: (() -> Unit)? = null
                override var onAddServiceClick: (() -> Unit)? = null
                override var onAddClientClick: (() -> Unit)? = null
                override var isAppointmentsStepDone: Boolean = false
                override var isServiceGroupStepDone: Boolean = false
                override var isServiceStepDone: Boolean = false
                override var isServiceStepEnabled: Boolean = false
                override var isAppointmentsStepEnabled: Boolean = false
                override var isServiceGroupStepEnabled: Boolean = false
                override var isClientStepEnabled: Boolean = false
                override var serviceGroupStepSubtitle: StringDesc = "".desc()
                override var serviceStepSubtitle: StringDesc = "".desc()
                override var clientStepSubtitle: StringDesc = "".desc()
                override var isClientStepDone: Boolean = false
                override var businesses: List<OnboardingBusinessItem> = emptyList()
                override var onBusinessClick: ((OnboardingBusinessItem) -> Unit)? = null
                override val refresh = FakeRefreshState()
            }
        }
        override val notifications = FakeNotificationState()
    }

    private class Fixture {
        val setupStatus = MutableStateFlow<DashboardSetupStatus>(DashboardSetupStatus.NoBusiness)
        val observeDashboardSetupStatus = mock<ObserveDashboardSetupStatus> {
            every { invoke() } returns setupStatus
        }
        val refreshBusinessInfo = mock<RefreshBusinessInfo> {
            everySuspend { invoke(any()) } returns Unit
        }
        val refreshDashboardSetupStatus = mock<RefreshDashboardSetupStatus> {
            everySuspend { invoke(any()) } returns Unit
        }
        val switchDashboardBusiness = mock<SwitchDashboardBusiness> {
            everySuspend { invoke(any()) } returns Unit
        }
        val joinBusiness = mock<JoinBusiness>()
        val createBusiness = mock<CreateBusiness>()
        val errorMapper = FakeErrorMapper()

        fun sut() = DashboardViewModel(
            observeDashboardSetupStatus = observeDashboardSetupStatus,
            refreshBusinessInfo = refreshBusinessInfo,
            refreshDashboardSetupStatus = refreshDashboardSetupStatus,
            switchDashboardBusiness = switchDashboardBusiness,
            joinBusiness = joinBusiness,
            createBusiness = createBusiness,
            stateFactory = object : DashboardStateFactory {
                override fun createDashboardState(initData: TabItemsState.InitData): DashboardState {
                    return FakeDashboardState(initData)
                }
            },
            vmArgs = VmArgs(errorMapper)
        )
    }

    private fun setupRequired(
        businessId: Uuid = Uuid.random(),
        isAppointmentsEnabled: Boolean = false,
        hasServiceGroups: Boolean = false,
        hasServices: Boolean = false,
        hasClients: Boolean = false,
        canEnableAppointments: Boolean = true,
        canManageServices: Boolean = true,
        canManageClients: Boolean = true,
        missingViewPermissions: Set<BusinessResource> = emptySet()
    ): DashboardSetupStatus.SetupRequired {
        return DashboardSetupStatus.SetupRequired(
            businessId = businessId,
            isAppointmentsEnabled = isAppointmentsEnabled,
            hasServiceGroups = hasServiceGroups,
            hasServices = hasServices,
            hasClients = hasClients,
            canEnableAppointments = canEnableAppointments,
            canManageServices = canManageServices,
            canManageClients = canManageClients,
            missingViewPermissions = missingViewPermissions
        )
    }

    private val DashboardViewModel.businessTab: TabItem
        get() = uiState.tabItems.items.single { it.id == TabItem.Id.BUSINESS }

    private fun DashboardViewModel.inputDialogs(): List<PresentationNotification.InputMessage> {
        return uiState.notifications.presentationNotification.filterIsInstance<PresentationNotification.InputMessage>()
    }

    private fun DashboardViewModel.submitJoinCode(code: String) {
        uiState.home.onboarding.onJoinBusinessClick?.invoke()
        inputDialogs().single().onConfirm(code)
    }

    private fun DashboardViewModel.submitBusinessName(name: String) {
        uiState.home.onboarding.onCreateBusinessClick?.invoke()
        inputDialogs().single().onConfirm(name)
    }

    private fun DashboardViewModel.messages(): List<PresentationNotification.Message> {
        return uiState.notifications.presentationNotification.filterIsInstance<PresentationNotification.Message>()
    }

    @Test
    fun `refreshes business info silently on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.refreshBusinessInfo(false) }
    }

    @Test
    fun `ignores business refresh failure`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.refreshBusinessInfo(any()) } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `shows create or join choice with business tab disabled without business`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(HomeContent.NoBusiness, sut.uiState.home.content)
        assertFalse(sut.businessTab.isEnabled)
    }

    @Test
    fun `shows setup with business tab enabled when business requires setup`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired()

        then()
        assertEquals(HomeContent.SetupRequired, sut.uiState.home.content)
        assertTrue(sut.businessTab.isEnabled)
    }

    @Test
    fun `shows awaiting setup with business name when user cannot set business up`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.AwaitingSetup(Uuid.random(), "Salon", emptySet())

        then()
        assertEquals(HomeContent.AwaitingSetup, sut.uiState.home.content)
        assertEquals(DashboardRes.strings.dashboard_onboarding_awaiting_message.format("Salon"), sut.uiState.home.onboarding.awaitingSetupMessage)
        assertTrue(sut.businessTab.isEnabled)
    }

    @Test
    fun `asks for clients access when user cannot view clients`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.AwaitingSetup(Uuid.random(), "Salon", setOf(BusinessResource.CLIENTS))

        then()
        assertEquals(DashboardRes.strings.dashboard_onboarding_awaiting_no_clients_access.format("Salon"), sut.uiState.home.onboarding.awaitingSetupMessage)
    }

    @Test
    fun `asks for services access when user cannot view services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.AwaitingSetup(Uuid.random(), "Salon", setOf(BusinessResource.SERVICES))

        then()
        assertEquals(DashboardRes.strings.dashboard_onboarding_awaiting_no_services_access.format("Salon"), sut.uiState.home.onboarding.awaitingSetupMessage)
    }

    @Test
    fun `asks for clients and services access when user cannot view either`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.AwaitingSetup(
            Uuid.random(),
            "Salon",
            setOf(BusinessResource.CLIENTS, BusinessResource.SERVICES)
        )

        then()
        assertEquals(
            DashboardRes.strings.dashboard_onboarding_awaiting_no_clients_services_access.format("Salon"),
            sut.uiState.home.onboarding.awaitingSetupMessage
        )
    }

    @Test
    fun `shows active plugin content when business is ready`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.Ready

        then()
        assertEquals(HomeContent.ActivePlugin, sut.uiState.home.content)
        assertTrue(sut.businessTab.isEnabled)
    }

    @Test
    fun `shows mapped error when setup status fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeDashboardSetupStatus() } returns failOnceThenSuspend()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `opens business name dialog on create business click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.home.onboarding.onCreateBusinessClick?.invoke()

        then()
        assertEquals(DashboardRes.strings.dashboard_create_dialog_title.desc(), sut.inputDialogs().single().title)
    }

    @Test
    fun `creates business with entered name`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createBusiness(any()) } returns stubBusiness()
        val sut = fixture.sut()

        whenn()
        sut.submitBusinessName("Salon")

        then()
        verifySuspend { fixture.createBusiness("Salon") }
    }

    @Test
    fun `shows local error when business name is empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createBusiness(any()) } throws CreateBusiness.Error.EmptyName()
        val sut = fixture.sut()

        whenn()
        sut.submitBusinessName("")

        then()
        assertEquals(listOf(DashboardRes.strings.dashboard_create_error_empty_name.desc()), sut.messages().map { it.message })
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows local error when invitation code is empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.joinBusiness(any()) } throws JoinBusiness.Error.EmptyCode()
        val sut = fixture.sut()

        whenn()
        sut.submitJoinCode("")

        then()
        assertEquals(listOf(DashboardRes.strings.dashboard_join_error_empty_code.desc()), sut.messages().map { it.message })
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows mapped error when business creation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createBusiness(any()) } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.submitBusinessName("Salon")

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `navigates to plugins of business that requires setup`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        fixture.setupStatus.value = setupRequired(id)
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.onEnablePluginsClick?.invoke()

        then()
        assertEquals(listOf<DashboardHomeNavigationDestination>(DashboardHomeNavigationDestination.EnablePlugins(id)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `does not navigate to plugins without business`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.home.onboarding.onEnablePluginsClick?.invoke()

        then()
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `does not navigate to plugins after business stops requiring setup`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupStatus.value = setupRequired()
        val sut = fixture.sut()
        fixture.setupStatus.value = DashboardSetupStatus.AwaitingSetup(Uuid.random(), "Salon", emptySet())

        whenn()
        sut.uiState.home.onboarding.onEnablePluginsClick?.invoke()

        then()
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `shows setup step progress of business that requires setup`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired(isAppointmentsEnabled = true, hasServiceGroups = true, hasServices = false, hasClients = true)

        then()
        val onboarding = sut.uiState.home.onboarding
        assertEquals(
            listOf(true, true, false, true),
            listOf(onboarding.isAppointmentsStepDone, onboarding.isServiceGroupStepDone, onboarding.isServiceStepDone, onboarding.isClientStepDone)
        )
    }

    @Test
    fun `locks service step until a service group exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired(hasServiceGroups = false)

        then()
        assertFalse(sut.uiState.home.onboarding.isServiceStepEnabled)
    }

    @Test
    fun `unlocks service step once a service group exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired(hasServiceGroups = true)

        then()
        assertTrue(sut.uiState.home.onboarding.isServiceStepEnabled)
    }

    @Test
    fun `enables every step the user can complete`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired(hasServiceGroups = true)

        then()
        val onboarding = sut.uiState.home.onboarding
        assertEquals(
            listOf(true, true, true, true),
            listOf(onboarding.isServiceGroupStepEnabled, onboarding.isServiceStepEnabled, onboarding.isClientStepEnabled, onboarding.isAppointmentsStepEnabled)
        )
    }

    @Test
    fun `disables plugin step without permission to enable appointments`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired(canEnableAppointments = false)

        then()
        assertFalse(sut.uiState.home.onboarding.isAppointmentsStepEnabled)
    }

    @Test
    fun `disables service group and service steps without permission to manage services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired(hasServiceGroups = true, canManageServices = false)

        then()
        val onboarding = sut.uiState.home.onboarding
        assertEquals(listOf(false, false), listOf(onboarding.isServiceGroupStepEnabled, onboarding.isServiceStepEnabled))
    }

    @Test
    fun `disables client step without permission to manage clients`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired(canManageClients = false)

        then()
        assertFalse(sut.uiState.home.onboarding.isClientStepEnabled)
    }

    @Test
    fun `shows regular hints on service and client steps`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired()

        then()
        val onboarding = sut.uiState.home.onboarding
        assertEquals(
            listOf(
                DashboardRes.strings.dashboard_onboarding_group_subtitle.desc(),
                DashboardRes.strings.dashboard_onboarding_service_subtitle.desc(),
                DashboardRes.strings.dashboard_onboarding_client_subtitle.desc()
            ),
            listOf(onboarding.serviceGroupStepSubtitle, onboarding.serviceStepSubtitle, onboarding.clientStepSubtitle)
        )
    }

    @Test
    fun `explains missing services access on service group and service steps`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired(canManageServices = false, missingViewPermissions = setOf(BusinessResource.SERVICES))

        then()
        val onboarding = sut.uiState.home.onboarding
        val noAccess = DashboardRes.strings.dashboard_onboarding_step_no_services_access.desc()
        assertEquals(listOf(noAccess, noAccess), listOf(onboarding.serviceGroupStepSubtitle, onboarding.serviceStepSubtitle))
    }

    @Test
    fun `explains missing clients access on client step`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired(canManageClients = false, missingViewPermissions = setOf(BusinessResource.CLIENTS))

        then()
        assertEquals(DashboardRes.strings.dashboard_onboarding_step_no_clients_access.desc(), sut.uiState.home.onboarding.clientStepSubtitle)
    }

    @Test
    fun `navigates to service groups on add service group click`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupStatus.value = setupRequired()
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.onAddServiceGroupClick?.invoke()

        then()
        assertEquals(listOf<DashboardHomeNavigationDestination>(DashboardHomeNavigationDestination.AddServiceGroup), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `navigates to add service of business once a service group exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        fixture.setupStatus.value = setupRequired(id, hasServiceGroups = true)
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.onAddServiceClick?.invoke()

        then()
        assertEquals(listOf<DashboardHomeNavigationDestination>(DashboardHomeNavigationDestination.AddService(id)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `does not navigate to add service before a service group exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupStatus.value = setupRequired(hasServiceGroups = false)
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.onAddServiceClick?.invoke()

        then()
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `navigates to add client of business that requires setup`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        fixture.setupStatus.value = setupRequired(id)
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.onAddClientClick?.invoke()

        then()
        assertEquals(listOf<DashboardHomeNavigationDestination>(DashboardHomeNavigationDestination.AddClient(id)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `does not navigate to setup steps without a business that requires setup`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupStatus.value = DashboardSetupStatus.Ready
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.onAddServiceGroupClick?.invoke()
        sut.uiState.home.onboarding.onAddServiceClick?.invoke()
        sut.uiState.home.onboarding.onAddClientClick?.invoke()

        then()
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `refreshes setup data of business that requires setup`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired(id)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.refreshDashboardSetupStatus(id) }
    }

    @Test
    fun `refreshes setup data only once while the same business progresses`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        fixture.setupStatus.value = setupRequired(id)
        fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired(id, isAppointmentsEnabled = true)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.refreshDashboardSetupStatus(any()) }
    }

    @Test
    fun `refreshes setup data again when another business requires setup`() = runUnitTest {
        given()
        val fixture = Fixture()
        val first = Uuid.random()
        val second = Uuid.random()
        fixture.setupStatus.value = setupRequired(first)
        fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired(second)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.refreshDashboardSetupStatus(second) }
    }

    @Test
    fun `refreshes setup data of business awaiting setup`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        fixture.sut()

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.AwaitingSetup(id, "Salon", emptySet())

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.refreshDashboardSetupStatus(id) }
    }

    @Test
    fun `does not refresh setup data when business is ready`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut()

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.Ready

        then()
        verifySuspend(VerifyMode.not) { fixture.refreshDashboardSetupStatus(any()) }
    }

    @Test
    fun `ignores setup data refresh failure`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.refreshDashboardSetupStatus(any()) } throws TestException()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = setupRequired()

        then()
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `joins business with entered code and shows success`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.joinBusiness(any()) } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.submitJoinCode("ABC123")

        then()
        verifySuspend { fixture.joinBusiness("ABC123") }
        val message = sut.uiState.notifications.presentationNotification
            .filterIsInstance<PresentationNotification.GlobalMessage>().single()
        assertEquals(PresentationNotification.GlobalMessage.State.SUCCESS, message.state)
    }

    @Test
    fun `shows already processed message when invitation was used`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.joinBusiness(any()) } throws JoinBusiness.Error.AlreadyProcessed(TestException())
        val sut = fixture.sut()

        whenn()
        sut.submitJoinCode("ABC123")

        then()
        assertEquals(1, sut.messages().size)
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows employee exists message when user already works there`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.joinBusiness(any()) } throws JoinBusiness.Error.EmployeeExists(TestException())
        val sut = fixture.sut()

        whenn()
        sut.submitJoinCode("ABC123")

        then()
        assertEquals(1, sut.messages().size)
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows mapped error when join fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.joinBusiness(any()) } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.submitJoinCode("ABC123")

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `shows business selection with business tab disabled when no business is selected`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.BusinessNotSelected(listOf(stubBusiness()))

        then()
        assertEquals(HomeContent.BusinessNotSelected, sut.uiState.home.content)
        assertFalse(sut.businessTab.isEnabled)
    }

    @Test
    fun `offers the user businesses for selection`() = runUnitTest {
        given()
        val fixture = Fixture()
        val salon = stubBusiness(name = "Salon")
        val barber = stubBusiness(name = "Barber")
        fixture.setupStatus.value = DashboardSetupStatus.BusinessNotSelected(listOf(salon, barber))

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            listOf(OnboardingBusinessItem(salon.id, "Salon"), OnboardingBusinessItem(barber.id, "Barber")),
            sut.uiState.home.onboarding.businesses
        )
    }

    @Test
    fun `offers no businesses for selection when the user has none`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.home.onboarding.businesses.isEmpty())
    }

    @Test
    fun `updates the offered businesses when they change`() = runUnitTest {
        given()
        val fixture = Fixture()
        val salon = stubBusiness(name = "Salon")
        fixture.setupStatus.value = DashboardSetupStatus.BusinessNotSelected(listOf(salon, stubBusiness(name = "Removed")))
        val sut = fixture.sut()

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.BusinessNotSelected(listOf(salon))

        then()
        assertEquals(listOf(OnboardingBusinessItem(salon.id, "Salon")), sut.uiState.home.onboarding.businesses)
    }

    @Test
    fun `switches the dashboard to the selected business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val salon = stubBusiness(name = "Salon")
        fixture.setupStatus.value = DashboardSetupStatus.BusinessNotSelected(listOf(salon))
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.onBusinessClick?.invoke(sut.uiState.home.onboarding.businesses.single())

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.switchDashboardBusiness(salon.id) }
    }

    @Test
    fun `shows mapped error when switching the dashboard business fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.switchDashboardBusiness(any()) } throws TestException()
        fixture.setupStatus.value = DashboardSetupStatus.BusinessNotSelected(listOf(stubBusiness()))
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.onBusinessClick?.invoke(sut.uiState.home.onboarding.businesses.single())

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `opens home when the dashboard business is lost while on the business tab`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupStatus.value = DashboardSetupStatus.Ready
        val sut = fixture.sut()
        sut.uiState.tabItems.selectedItemId = TabItem.Id.BUSINESS

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.NoBusiness

        then()
        assertEquals(TabItem.Id.HOME, sut.uiState.tabItems.selectedItemId)
    }

    @Test
    fun `opens home when the dashboard business is lost while on the settings tab`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupStatus.value = DashboardSetupStatus.AwaitingSetup(Uuid.random(), "Salon", emptySet())
        val sut = fixture.sut()
        sut.uiState.tabItems.selectedItemId = TabItem.Id.SETTINGS

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.NoBusiness

        then()
        assertEquals(TabItem.Id.HOME, sut.uiState.tabItems.selectedItemId)
    }

    @Test
    fun `opens home when the dashboard business is lost but other businesses remain`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupStatus.value = DashboardSetupStatus.Ready
        val sut = fixture.sut()
        sut.uiState.tabItems.selectedItemId = TabItem.Id.SETTINGS

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.BusinessNotSelected(listOf(stubBusiness()))

        then()
        assertEquals(TabItem.Id.HOME, sut.uiState.tabItems.selectedItemId)
    }

    @Test
    fun `keeps the selected tab while the dashboard business stays available`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupStatus.value = setupRequired()
        val sut = fixture.sut()
        sut.uiState.tabItems.selectedItemId = TabItem.Id.SETTINGS

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.Ready

        then()
        assertEquals(TabItem.Id.SETTINGS, sut.uiState.tabItems.selectedItemId)
    }

    @Test
    fun `keeps the selected tab when the user never had a dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.uiState.tabItems.selectedItemId = TabItem.Id.SETTINGS

        whenn()
        fixture.setupStatus.value = DashboardSetupStatus.NoBusiness

        then()
        assertEquals(TabItem.Id.SETTINGS, sut.uiState.tabItems.selectedItemId)
    }

    @Test
    fun `refreshes business info on pull to refresh`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.refresh.onRefresh()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.refreshBusinessInfo(false) }
    }

    @Test
    fun `refreshes setup data of business that requires setup on pull to refresh`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        fixture.setupStatus.value = setupRequired(id)
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.refresh.onRefresh()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.refreshDashboardSetupStatus(id) }
    }

    @Test
    fun `refreshes setup data of business awaiting setup on pull to refresh`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        fixture.setupStatus.value = DashboardSetupStatus.AwaitingSetup(id, "Salon", emptySet())
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.refresh.onRefresh()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.refreshDashboardSetupStatus(id) }
    }

    @Test
    fun `does not refresh setup data on pull to refresh without a dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupStatus.value = DashboardSetupStatus.BusinessNotSelected(listOf(stubBusiness()))
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.refresh.onRefresh()

        then()
        verifySuspend(VerifyMode.not) { fixture.refreshDashboardSetupStatus(any()) }
    }

    @Test
    fun `shows refreshing until pull to refresh completes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val gate = CompletableDeferred<Unit>()
        fixture.setupStatus.value = setupRequired()
        everySuspend { fixture.refreshDashboardSetupStatus(any()) } calls { gate.await() }
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.refresh.onRefresh()
        val isRefreshingWhileLoading = sut.uiState.home.onboarding.refresh.isRefreshing
        gate.complete(Unit)

        then()
        assertEquals(listOf(true, false), listOf(isRefreshingWhileLoading, sut.uiState.home.onboarding.refresh.isRefreshing))
    }

    @Test
    fun `shows mapped error and stops refreshing when pull to refresh fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupStatus.value = setupRequired()
        everySuspend { fixture.refreshDashboardSetupStatus(any()) } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.uiState.home.onboarding.refresh.onRefresh()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.home.onboarding.refresh.isRefreshing)
    }
}
