package me.bookk.feature.services.presentation.group.list

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.designsystem.test.tap
import me.bookk.feature.services.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.services.domain.api.group.DeleteServiceGroup
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.presentation.FakeServicesStateFactory
import me.bookk.feature.services.presentation.stubGroup
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class ServiceGroupListViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture {
        val businessId = Uuid.random()
        val groups = MutableStateFlow<List<ServiceGroup>>(emptyList())
        val getServiceGroups = mock<GetServiceGroups> {
            every { flow() } returns groups
            everySuspend { refresh(any()) } returns emptyList()
        }
        val deleteServiceGroup = mock<DeleteServiceGroup>()
        val observeCurrentBusinessId = mock<ObserveCurrentBusinessId> {
            every { invoke() } returns MutableStateFlow<Uuid?>(businessId)
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = ServiceGroupListViewModel(
            getServiceGroups = getServiceGroups,
            deleteServiceGroup = deleteServiceGroup,
            observeCurrentBusinessId = observeCurrentBusinessId,
            stateFactory = FakeServicesStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `refreshes groups of current business on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getServiceGroups.refresh(fixture.businessId) }
    }

    @Test
    fun `renders cached groups`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()

        whenn()
        fixture.groups.value = listOf(stubGroup("Hair"), stubGroup("Nails"))

        then()
        assertEquals(listOf("Hair", "Nails"), sut.uiState.groups.items.map { it.name })
    }

    @Test
    fun `filters groups by search query`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.groups.value = listOf(stubGroup("Hair"), stubGroup("Nails"))

        whenn()
        (sut.uiState.search as FakeTextFieldState).type("nai")

        then()
        assertEquals(listOf("Nails"), sut.uiState.groups.items.map { it.name })
    }

    @Test
    fun `restores groups when search is cleared`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.groups.value = listOf(stubGroup("Hair"), stubGroup("Nails"))
        (sut.uiState.search as FakeTextFieldState).type("nai")

        whenn()
        (sut.uiState.search as FakeTextFieldState).type(" ")

        then()
        assertEquals(2, sut.uiState.groups.items.size)
    }

    @Test
    fun `opens add group dialog from app bar action`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.actions.items.single().onClick()

        then()
        assertTrue(sut.uiState.isAddGroupDialogVisible)
    }

    @Test
    fun `deletes group after confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        val group = stubGroup("Hair")
        everySuspend { fixture.deleteServiceGroup(any()) } returns Unit
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.groups.value = listOf(group)
        sut.uiState.groups.items.single().onDeleteClick()

        whenn()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>().tap(ActionType.NEGATIVE)

        then()
        verifySuspend { fixture.deleteServiceGroup(group) }
        assertFalse(sut.uiState.refreshState.isRefreshing)
    }

    @Test
    fun `shows mapped error when delete fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteServiceGroup(any()) } throws TestException()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.groups.value = listOf(stubGroup())
        sut.uiState.groups.items.single().onDeleteClick()

        whenn()
        sut.uiState.notifications.presentationNotification.filterIsInstance<PresentationNotification.Message>().single().tap(ActionType.NEGATIVE)

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `shows mapped error when group observation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getServiceGroups.flow() } returns failOnceThenSuspend()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf<ServiceGroupListDestination>(ServiceGroupListDestination.Back), sut.uiState.navigation.navigationDestination)
    }
}
