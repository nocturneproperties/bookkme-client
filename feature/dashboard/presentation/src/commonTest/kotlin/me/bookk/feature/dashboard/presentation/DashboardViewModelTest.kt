package me.bookk.feature.dashboard.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessIdChanges
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
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
                override var isBusinessStepDone: Boolean = false
                override var isPluginsStepUnlocked: Boolean = false
                override var onCreateBusinessClick: (() -> Unit)? = null
                override var onEnablePluginsClick: (() -> Unit)? = null
            }
        }
        override val notifications = FakeNotificationState()
    }

    private class Fixture {
        val businessId = MutableStateFlow<Uuid?>(null)
        val pluginEnabled = MutableStateFlow<Boolean?>(null)
        val observeDashboardBusinessIdChanges = mock<ObserveDashboardBusinessIdChanges> {
            every { invoke() } returns businessId
        }
        val isAppointmentsPluginEnabled = mock<IsAppointmentsPluginEnabled> {
            every { flow(any()) } returns pluginEnabled
        }
        val refreshBusinessInfo = mock<RefreshBusinessInfo> {
            everySuspend { invoke(any()) } returns Unit
        }

        fun sut() = DashboardViewModel(
            observeDashboardBusinessIdChanges = observeDashboardBusinessIdChanges,
            isAppointmentsPluginEnabled = isAppointmentsPluginEnabled,
            refreshBusinessInfo = refreshBusinessInfo,
            stateFactory = object : DashboardStateFactory {
                override fun createDashboardState(initData: TabItemsState.InitData): DashboardState {
                    return FakeDashboardState(initData)
                }
            },
            vmArgs = VmArgs(FakeErrorMapper())
        )
    }

    private val DashboardViewModel.businessTab: TabItem
        get() = uiState.tabItems.items.single { it.id == TabItem.Id.BUSINESS }

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
    fun `shows onboarding with business tab disabled without business`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(HomeContent.Onboarding, sut.uiState.home.content)
        assertFalse(sut.businessTab.isEnabled)
        assertFalse(sut.uiState.home.onboarding.isBusinessStepDone)
    }

    @Test
    fun `unlocks business tab and plugin step once a business exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.businessId.value = Uuid.random()

        then()
        assertTrue(sut.businessTab.isEnabled)
        assertTrue(sut.uiState.home.onboarding.isBusinessStepDone)
        assertTrue(sut.uiState.home.onboarding.isPluginsStepUnlocked)
        assertEquals(HomeContent.Onboarding, sut.uiState.home.content)
    }

    @Test
    fun `shows active plugin content when appointments plugin is enabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.businessId.value = Uuid.random()
        val sut = fixture.sut()

        whenn()
        fixture.pluginEnabled.value = true

        then()
        assertEquals(HomeContent.ActivePlugin, sut.uiState.home.content)
    }

    @Test
    fun `navigates to create business from onboarding`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.home.onboarding.onCreateBusinessClick?.invoke()

        then()
        assertEquals(listOf<DashboardHomeNavigationDestination>(DashboardHomeNavigationDestination.CreateBusiness), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `navigates to plugins of current business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        fixture.businessId.value = id
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
}
