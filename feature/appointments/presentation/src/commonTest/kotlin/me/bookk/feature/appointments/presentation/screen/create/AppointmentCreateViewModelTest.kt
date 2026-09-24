package me.bookk.feature.appointments.presentation.screen.create

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import library.money.api.Money
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakePickerFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.appointments.domain.api.CreateAppointment
import me.bookk.feature.appointments.domain.api.GetAppointmentOptions
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentDraft
import me.bookk.feature.appointments.domain.api.entity.AppointmentOptions
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.appointments.presentation.FakeAppointmentsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class AppointmentCreateViewModelTest {

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
        val client = ClientSnapshot.stub()
        val haircut = ServiceSnapshot(Uuid.random(), "Haircut", Uuid.random(), Money(10000L, Money.SupportedCurrency.UAH), 30.minutes)
        val coloring = ServiceSnapshot(Uuid.random(), "Coloring", Uuid.random(), Money(5000L, Money.SupportedCurrency.UAH), 60.minutes)
        val getAppointmentOptions = mock<GetAppointmentOptions> {
            everySuspend { invoke(any()) } returns AppointmentOptions(
                settings = AppointmentSettings.stub(),
                clients = listOf(client),
                services = listOf(haircut, coloring)
            )
        }
        val createAppointment = mock<CreateAppointment>()
        val errorMapper = FakeErrorMapper()
        val drafts = Capture.slot<AppointmentDraft>()

        fun sut() = AppointmentCreateViewModel(
            businessId = businessId,
            getAppointmentOptions = getAppointmentOptions,
            createAppointment = createAppointment,
            dateLocalizer = FakeDateLocalizer(),
            stateFactory = FakeAppointmentsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun AppointmentCreateViewModel.fillValidForm() {
        (uiState.clientPicker as FakePickerFieldState<SimplePickerPresentation<ClientSnapshot>>).pick(uiState.clientPicker.options.first())
        uiState.servicePicker.onItemsPicked(listOf(uiState.servicePicker.options.first()))
        uiState.datePicker.datePicker.onDatePicked?.invoke(LocalDate(2030, 1, 10))
        uiState.timePicker.timePicker.onTimePicked?.invoke(LocalTime(14, 30))
    }

    @Test
    fun `loads clients and services as picker options`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(listOf(fixture.client), sut.uiState.clientPicker.options.map { it.domain })
        assertEquals(listOf(fixture.haircut, fixture.coloring), sut.uiState.servicePicker.options.map { it.item })
    }

    @Test
    fun `enables create only when client services date and time are picked`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.fillValidForm()

        then()
        assertTrue(sut.uiState.create.isEnabled)
    }

    @Test
    fun `keeps create disabled without time`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        @Suppress("UNCHECKED_CAST")
        (sut.uiState.clientPicker as FakePickerFieldState<SimplePickerPresentation<ClientSnapshot>>).pick(sut.uiState.clientPicker.options.first())
        sut.uiState.servicePicker.onItemsPicked(listOf(sut.uiState.servicePicker.options.first()))
        sut.uiState.datePicker.datePicker.onDatePicked?.invoke(LocalDate(2030, 1, 10))

        then()
        assertFalse(sut.uiState.create.isEnabled)
    }

    @Test
    fun `sums subtotal of picked services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.servicePicker.onItemsPicked(sut.uiState.servicePicker.options.toList())

        then()
        assertEquals((fixture.haircut.price + fixture.coloring.price).toString(), sut.uiState.subtotalPrice)
    }

    @Test
    fun `clears subtotal when all services are removed`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.uiState.servicePicker.onItemsPicked(sut.uiState.servicePicker.options.toList())

        whenn()
        sut.uiState.servicePicker.onItemsRemoveRequested(sut.uiState.servicePicker.selectedItems.toList())

        then()
        assertEquals("", sut.uiState.subtotalPrice)
        assertEquals("".desc(), sut.uiState.subtotalLabel)
    }

    @Test
    fun `does not duplicate a service picked twice`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        val first = sut.uiState.servicePicker.options.first()
        sut.uiState.servicePicker.onItemsPicked(listOf(first))

        whenn()
        sut.uiState.servicePicker.onItemsPicked(listOf(first))

        then()
        assertEquals(1, sut.uiState.servicePicker.selectedItems.size)
    }

    @Test
    fun `creates appointment draft from picked values and goes back`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAppointment(capture(fixture.drafts)) } returns Appointment.stub()
        val sut = fixture.sut()
        sut.fillValidForm()
        sut.uiState.note.text = "Window seat"

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        assertEquals(
            AppointmentDraft(
                businessId = fixture.businessId,
                client = fixture.client,
                services = listOf(fixture.haircut),
                date = LocalDateTime(2030, 1, 10, 14, 30),
                note = "Window seat"
            ),
            fixture.drafts.get()
        )
        assertEquals(listOf<AppointmentCreateDestination>(AppointmentCreateDestination.Back), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `does not create without picked client`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.not) { fixture.createAppointment(any()) }
    }

    @Test
    fun `shows message when appointment overlaps`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAppointment(any()) } throws CreateAppointment.Error.AppointmentOverlap()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `marks date field invalid when date is not allowed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAppointment(any()) } throws CreateAppointment.Error.DateIsNotAllowed()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        assertEquals(ValidationState.ERROR, sut.uiState.datePicker.textField.validationState)
        assertFalse(sut.uiState.create.isEnabled)
    }

    @Test
    fun `marks time field invalid when time is not allowed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAppointment(any()) } throws CreateAppointment.Error.TimeIsNotAllowed()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        assertEquals(ValidationState.ERROR, sut.uiState.timePicker.textField.validationState)
        assertFalse(sut.uiState.create.isEnabled)
    }

    @Test
    fun `clears date and time errors when a new time is picked`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAppointment(any()) } throws CreateAppointment.Error.TimeIsNotAllowed()
        val sut = fixture.sut()
        sut.fillValidForm()
        sut.uiState.create.onClick?.invoke()

        whenn()
        sut.uiState.timePicker.timePicker.onTimePicked?.invoke(LocalTime(15, 0))

        then()
        assertEquals(ValidationState.DEFAULT, sut.uiState.timePicker.textField.validationState)
        assertTrue(sut.uiState.create.isEnabled)
    }

    @Test
    fun `shows mapped error when creation fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAppointment(any()) } throws TestException()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.create.isLoading)
    }

    @Test
    fun `shows mapped error when options cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAppointmentOptions(any()) } throws TestException()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }
}
