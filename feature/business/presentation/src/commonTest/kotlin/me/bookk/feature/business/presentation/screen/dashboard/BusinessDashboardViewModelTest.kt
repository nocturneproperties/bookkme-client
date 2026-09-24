package me.bookk.feature.business.presentation.screen.dashboard

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.designsystem.uistate.BusinessMenuItem
import me.bookk.feature.business.domain.api.business.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.business.JoinBusiness
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.domain.api.entity.DashboardOverview
import me.bookk.feature.business.presentation.FakeBusinessStateFactory
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardSection
import me.bookk.feature.business.presentation.stubBusiness
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class BusinessDashboardViewModelTest {

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
        val overview = MutableStateFlow<DashboardOverview?>(null)
        val businesses = MutableStateFlow<List<Business>>(emptyList())
        val getAvailableDashboardFeatures = mock<GetAvailableDashboardFeatures> {
            every { invoke() } returns overview
        }
        val observeUserBusinessesChanges = mock<ObserveUserBusinessesChanges> {
            every { invoke() } returns businesses
        }
        val switchDashboardBusiness = mock<SwitchDashboardBusiness>()
        val joinBusiness = mock<JoinBusiness>()
        val errorMapper = FakeErrorMapper()

        fun sut() = BusinessDashboardViewModel(
            getAvailableDashboardFeatures = getAvailableDashboardFeatures,
            observeUserBusinessesChanges = observeUserBusinessesChanges,
            switchDashboardBusiness = switchDashboardBusiness,
            joinBusiness = joinBusiness,
            stateFactory = FakeBusinessStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    private fun BusinessDashboardViewModel.submitJoinCode(code: String) {
        uiState.businessMenu.onJoinClick?.invoke()
        val dialog = uiState.notifications.presentationNotification.removeFirstInput()
        dialog.onConfirm(code)
    }

    private fun List<PresentationNotification>.removeFirstInput(): PresentationNotification.InputMessage {
        return filterIsInstance<PresentationNotification.InputMessage>().single()
    }

    @Test
    fun `selects business from dashboard overview`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness(name = "Salon")
        val sut = fixture.sut()

        whenn()
        fixture.overview.value = DashboardOverview(business, emptySet())

        then()
        assertEquals(business.id, sut.uiState.businessMenu.selectedBusinessId)
    }

    @Test
    fun `builds business and appointments sections from features`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        val sut = fixture.sut()

        whenn()
        fixture.overview.value = DashboardOverview(
            business,
            setOf(DashboardFeature.BUSINESS, DashboardFeature.CLIENTS, DashboardFeature.APPOINTMENTS)
        )

        then()
        val sections = sut.uiState.sections
        assertEquals(2, sections.size)
        assertIs<BusinessDashboardSection.Business>(sections[0])
        assertIs<BusinessDashboardSection.Appointments>(sections[1])
    }

    @Test
    fun `shows no sections when no section feature is available`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.overview.value = DashboardOverview(stubBusiness(), setOf(DashboardFeature.CLIENTS))

        then()
        assertTrue(sut.uiState.sections.isEmpty())
    }

    @Test
    fun `adds shop section when shop feature is available`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.overview.value = DashboardOverview(stubBusiness(), setOf(DashboardFeature.SHOP))

        then()
        assertIs<BusinessDashboardSection.Shop>(sut.uiState.sections.single())
    }

    @Test
    fun `lists user businesses in menu`() = runUnitTest {
        given()
        val fixture = Fixture()
        val first = stubBusiness(name = "First")
        val second = stubBusiness(name = "Second")
        val sut = fixture.sut()

        whenn()
        fixture.businesses.value = listOf(first, second)

        then()
        assertEquals(
            listOf(BusinessMenuItem(first.id, "First"), BusinessMenuItem(second.id, "Second")),
            sut.uiState.businessMenu.items
        )
    }

    @Test
    fun `switches dashboard business on menu click`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        everySuspend { fixture.switchDashboardBusiness(any()) } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.uiState.businessMenu.onBusinessClick?.invoke(id)

        then()
        verifySuspend { fixture.switchDashboardBusiness(id) }
    }

    @Test
    fun `opens create business sheet on create click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.businessMenu.onCreateClick?.invoke()

        then()
        assertTrue(sut.uiState.isCreateBusinessSheetVisible)
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
        assertEquals(1, sut.uiState.notifications.presentationNotification.filterIsInstance<PresentationNotification.Message>().size)
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
        assertEquals(1, sut.uiState.notifications.presentationNotification.filterIsInstance<PresentationNotification.Message>().size)
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
    fun `shows mapped error when dashboard overview fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getAvailableDashboardFeatures() } returns failOnceThenSuspend()

        whenn()
        val sut = fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        sut.uiState.notifications.assertSingle<PresentationNotification.GlobalMessage>()
    }
}
