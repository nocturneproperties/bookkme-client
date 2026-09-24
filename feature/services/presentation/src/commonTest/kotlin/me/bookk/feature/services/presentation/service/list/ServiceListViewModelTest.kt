package me.bookk.feature.services.presentation.service.list

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
import me.bookk.feature.services.domain.api.service.DeleteService
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.presentation.FakeServicesStateFactory
import me.bookk.feature.services.presentation.stubGroup
import me.bookk.feature.services.presentation.stubService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.uuid.Uuid

class ServiceListViewModelTest {

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
        val services = MutableStateFlow<List<Service>>(emptyList())
        val currentBusinessId = MutableStateFlow<Uuid?>(businessId)
        val getServices = mock<GetServices> {
            every { flow() } returns services
            everySuspend { refresh(any()) } returns emptyList()
        }
        val deleteService = mock<DeleteService>()
        val observeCurrentBusinessId = mock<ObserveCurrentBusinessId> {
            every { invoke() } returns currentBusinessId
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = ServiceListViewModel(
            getServices = getServices,
            deleteService = deleteService,
            observeCurrentBusinessId = observeCurrentBusinessId,
            stateFactory = FakeServicesStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `refreshes services of current business on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getServices.refresh(fixture.businessId) }
    }

    @Test
    fun `groups services by their group`() = runUnitTest {
        given()
        val fixture = Fixture()
        val hair = stubGroup("Hair")
        val nails = stubGroup("Nails")
        val sut = fixture.sut()
        advanceUntilIdle()

        whenn()
        fixture.services.value = listOf(
            stubService("Cut", hair),
            stubService("Manicure", nails),
            stubService("Color", hair)
        )

        then()
        val sections = sut.uiState.services.items
        assertEquals(listOf("Hair", "Nails"), sections.map { it.name })
        assertEquals(listOf("Cut", "Color"), sections[0].items.map { it.title })
    }

    @Test
    fun `filters services by search query and drops empty groups`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.services.value = listOf(stubService("Cut", stubGroup("Hair")), stubService("Manicure", stubGroup("Nails")))

        whenn()
        (sut.uiState.searchField as FakeTextFieldState).type("mani")

        then()
        assertEquals(listOf("Nails"), sut.uiState.services.items.map { it.name })
    }

    @Test
    fun `navigates to service details on item click`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.services.value = listOf(service)
        val section = sut.uiState.services.items.single()

        whenn()
        section.onItemClick(section.items.single())

        then()
        assertEquals(listOf<ServiceListDestination>(ServiceListDestination.ServiceDetails(service.id)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `deletes service after confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        everySuspend { fixture.deleteService(any()) } returns Unit
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.services.value = listOf(service)
        val section = sut.uiState.services.items.single()
        section.onItemDeleteClick(section.items.single())

        whenn()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>().tap(ActionType.NEGATIVE)

        then()
        verifySuspend { fixture.deleteService(service) }
        assertFalse(sut.uiState.refreshState.isRefreshing)
    }

    @Test
    fun `shows mapped error when delete fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteService(any()) } throws TestException()
        val sut = fixture.sut()
        advanceUntilIdle()
        fixture.services.value = listOf(stubService())
        val section = sut.uiState.services.items.single()
        section.onItemDeleteClick(section.items.single())

        whenn()
        sut.uiState.notifications.presentationNotification.filterIsInstance<PresentationNotification.Message>().single().tap(ActionType.NEGATIVE)

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `navigates to add service for current business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.appBar.actions.items.single().onClick()

        then()
        assertEquals(listOf<ServiceListDestination>(ServiceListDestination.AddService(fixture.businessId)), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `navigates to groups from groups section`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.groupsSection.onClick()

        then()
        assertEquals(listOf<ServiceListDestination>(ServiceListDestination.ServiceGroups), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `reloads when business changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val other = Uuid.random()
        fixture.sut()

        whenn()
        fixture.currentBusinessId.value = other

        then()
        verifySuspend { fixture.getServices.refresh(other) }
    }

    @Test
    fun `shows mapped error when service observation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getServices.flow() } returns failOnceThenSuspend()

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
        assertEquals(listOf<ServiceListDestination>(ServiceListDestination.Back), sut.uiState.navigation.navigationDestination)
    }
}
