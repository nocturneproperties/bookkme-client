package me.bookk.feature.dashboard.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
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
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.domain.api.business.JoinBusiness
import me.bookk.feature.business.domain.api.business.ObserveDashboardSetupStatus
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.entity.DashboardSetupStatus
import me.bookk.feature.dashboard.presentation.state.DashboardHomeState
import me.bookk.feature.dashboard.presentation.state.DashboardState
import me.bookk.feature.dashboard.presentation.state.HomeContent
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
        val joinBusiness = mock<JoinBusiness>()
        val createBusiness = mock<CreateBusiness>()
        val errorMapper = FakeErrorMapper()

        fun sut() = DashboardViewModel(
            observeDashboardSetupStatus = observeDashboardSetupStatus,
            refreshBusinessInfo = refreshBusinessInfo,
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
        fixture.setupStatus.value = DashboardSetupStatus.SetupRequired(Uuid.random())

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
        fixture.setupStatus.value = DashboardSetupStatus.AwaitingSetup("Salon")

        then()
        assertEquals(HomeContent.AwaitingSetup, sut.uiState.home.content)
        assertEquals(DashboardRes.strings.dashboard_onboarding_awaiting_message.format("Salon"), sut.uiState.home.onboarding.awaitingSetupMessage)
        assertTrue(sut.businessTab.isEnabled)
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
        fixture.setupStatus.value = DashboardSetupStatus.SetupRequired(id)
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
        fixture.setupStatus.value = DashboardSetupStatus.SetupRequired(Uuid.random())
        val sut = fixture.sut()
        fixture.setupStatus.value = DashboardSetupStatus.AwaitingSetup("Salon")

        whenn()
        sut.uiState.home.onboarding.onEnablePluginsClick?.invoke()

        then()
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
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
}
