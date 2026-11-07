package me.bookk.feature.appointments.presentation.screen.create

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import library.money.api.Money
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakePickerFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.appointments.domain.api.CreateAppointment
import me.bookk.feature.appointments.domain.api.GetAppointmentOptions
import me.bookk.feature.appointments.domain.api.ObserveAppointmentOptions
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentDraft
import me.bookk.feature.appointments.domain.api.entity.AppointmentOptions
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
        val haircut = ServiceSnapshot(
            Uuid.random(),
            "Haircut",
            Uuid.random(),
            Money(10000L, Money.SupportedCurrency.UAH),
            30.minutes,
        )
        val coloring = ServiceSnapshot(
            Uuid.random(),
            "Coloring",
            Uuid.random(),
            Money(5000L, Money.SupportedCurrency.UAH),
            60.minutes,
        )
        val options = MutableStateFlow(
            AppointmentOptions(
                clients = listOf(client),
                services = listOf(haircut, coloring),
            ),
        )
        val observeAppointmentOptions = mock<ObserveAppointmentOptions> {
            every { invoke() } returns options
        }
        val getAppointmentOptions = mock<GetAppointmentOptions> {
            everySuspend { invoke(any()) } returns options.value
        }
        val createAppointment = mock<CreateAppointment>()
        val errorMapper = FakeErrorMapper()
        val drafts = Capture.slot<AppointmentDraft>()

        fun sut() = AppointmentCreateViewModel(
            businessId = businessId,
            getAppointmentOptions = getAppointmentOptions,
            observeAppointmentOptions = observeAppointmentOptions,
            createAppointment = createAppointment,
            dateLocalizer = FakeDateLocalizer(),
            stateFactory = FakeAppointmentsStateFactory(),
            vmArgs = VmArgs(errorMapper),
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun AppointmentCreateViewModel.fillValidForm() {
        (uiState.clientPicker as FakePickerFieldState<SimplePickerPresentation<ClientSnapshot>>)
            .pick(uiState.clientPicker.options.first())
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
        assertEquals(
            listOf(fixture.haircut, fixture.coloring),
            sut.uiState.servicePicker.options.map { it.item },
        )
    }

    @Test
    fun `refreshes appointment options of business on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getAppointmentOptions(fixture.businessId) }
    }

    @Test
    fun `updates picker options when observed options change`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        val newClient = ClientSnapshot.stub()

        whenn()
        fixture.options.value = AppointmentOptions(
            clients = listOf(fixture.client, newClient),
            services = listOf(fixture.coloring),
        )

        then()
        assertEquals(
            listOf(fixture.client, newClient),
            sut.uiState.clientPicker.options.map { it.domain },
        )
        assertEquals(listOf(fixture.coloring), sut.uiState.servicePicker.options.map { it.item })
    }

    @Test
    fun `updates selected service details and subtotal from observed options`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.fillValidForm()
        val updated = fixture.haircut.copy(
            name = "Updated haircut",
            price = Money(12000L, Money.SupportedCurrency.UAH),
            duration = 45.minutes,
        )

        whenn()
        fixture.options.value = AppointmentOptions(
            clients = fixture.options.value.clients,
            services = listOf(updated),
        )

        then()
        assertEquals(listOf(updated), sut.uiState.servicePicker.selectedItems.map { it.item })
        assertEquals(updated.price.toString(), sut.uiState.subtotalPrice)
        assertTrue(sut.uiState.create.isEnabled)
    }

    @Test
    fun `removes missing selections and recalculates subtotal`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.fillValidForm()
        sut.uiState.servicePicker.onItemsPicked(listOf(sut.uiState.servicePicker.options.last()))

        whenn()
        fixture.options.value = AppointmentOptions(
            clients = fixture.options.value.clients,
            services = listOf(fixture.coloring),
        )

        then()
        assertEquals(
            listOf(fixture.coloring),
            sut.uiState.servicePicker.selectedItems.map { it.item },
        )
        assertEquals(fixture.coloring.price.toString(), sut.uiState.subtotalPrice)
        assertTrue(sut.uiState.create.isEnabled)
    }

    @Test
    fun `disables creation and clears subtotal when all selections disappear`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        fixture.options.value = AppointmentOptions(
            clients = fixture.options.value.clients,
            services = listOf(fixture.coloring),
        )

        then()
        assertTrue(sut.uiState.servicePicker.selectedItems.isEmpty())
        assertEquals("", sut.uiState.subtotalPrice)
        assertEquals("".desc(), sut.uiState.subtotalLabel)
        assertFalse(sut.uiState.create.isEnabled)
    }

    @Test
    fun `preserves other form fields when service options change`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.fillValidForm()
        sut.uiState.note.text = "Window seat"

        whenn()
        fixture.options.value = AppointmentOptions(
            clients = fixture.options.value.clients,
            services = listOf(fixture.haircut.copy(duration = 45.minutes)),
        )

        then()
        assertEquals(fixture.client, sut.uiState.clientPicker.selectedItem?.domain)
        assertEquals(LocalDate(2030, 1, 10), sut.uiState.datePicker.datePicker.pickedDate)
        assertEquals(LocalTime(14, 30), sut.uiState.timePicker.timePicker.pickedTime)
        assertEquals("Window seat", sut.uiState.note.text)
        verifySuspend(VerifyMode.exactly(0)) { fixture.createAppointment(any()) }
    }

    @Test
    fun `keeps creation disabled while refreshed services arrive during a pending booking`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val result = CompletableDeferred<Appointment>()
            everySuspend { fixture.createAppointment(any()) } calls { result.await() }
            val sut = fixture.sut()
            sut.fillValidForm()
            sut.uiState.create.onClick?.invoke()

            whenn()
            fixture.options.value = AppointmentOptions(
                clients = fixture.options.value.clients,
                services = listOf(fixture.haircut.copy(duration = 45.minutes)),
            )

            then()
            assertTrue(sut.uiState.create.isLoading)
            assertFalse(sut.uiState.create.isEnabled)
            result.completeExceptionally(CreateAppointment.Error.DurationChanged())
            assertFalse(sut.uiState.create.isLoading)
            assertTrue(sut.uiState.create.isEnabled)
        }

    @Test
    fun `does not retry booking automatically after refreshed services and a quote rejection`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val updated = fixture.haircut.copy(price = Money(12000L, Money.SupportedCurrency.UAH))
            everySuspend { fixture.createAppointment(any()) } calls {
                fixture.options.value = AppointmentOptions(
                    clients = fixture.options.value.clients,
                    services = listOf(updated),
                )
                throw CreateAppointment.Error.PriceChanged()
            }
            val sut = fixture.sut()
            sut.fillValidForm()

            whenn()
            sut.uiState.create.onClick?.invoke()

            then()
            assertEquals(updated.price.toString(), sut.uiState.subtotalPrice)
            assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
            assertEquals(
                AppointmentsRes.strings.appointments_create_price_changed_error.desc(),
                sut.uiState.notifications.assertSingle<PresentationNotification.Message>().message,
            )
            verifySuspend(VerifyMode.exactly(1)) { fixture.createAppointment(any()) }
        }

    @Test
    fun `submits refreshed selected services on the next explicit booking attempt`() = runUnitTest {
        given()
        val fixture = Fixture()
        val updated = fixture.haircut.copy(price = Money(12000L, Money.SupportedCurrency.UAH))
        val sut = fixture.sut()
        sut.fillValidForm()
        fixture.options.value = AppointmentOptions(
            clients = fixture.options.value.clients,
            services = listOf(updated),
        )
        everySuspend { fixture.createAppointment(capture(fixture.drafts)) } returns
            Appointment.stub()

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        assertEquals(listOf(updated), fixture.drafts.get().services)
    }

    @Test
    fun `shows mapped error when options observation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeAppointmentOptions() } returns failOnceThenSuspend()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `picks services on a chooser screen`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(PickerFieldState.PickerType.SCREEN, sut.uiState.servicePicker.pickerType)
    }

    @Test
    fun `shows a placeholder while no services are picked`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            AppointmentsRes.strings.appointments_create_services_empty.desc(),
            sut.uiState.servicePicker.placeholder,
        )
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
        (sut.uiState.clientPicker as FakePickerFieldState<SimplePickerPresentation<ClientSnapshot>>)
            .pick(sut.uiState.clientPicker.options.first())
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
        assertEquals(
            (fixture.haircut.price + fixture.coloring.price).toString(),
            sut.uiState.subtotalPrice,
        )
    }

    @Test
    fun `clears subtotal when all services are removed`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.uiState.servicePicker.onItemsPicked(sut.uiState.servicePicker.options.toList())

        whenn()
        sut.uiState.servicePicker.onItemsRemoveRequested(
            sut.uiState.servicePicker.selectedItems.toList(),
        )

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
        everySuspend { fixture.createAppointment(capture(fixture.drafts)) } returns
            Appointment.stub()
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
                note = "Window seat",
            ),
            fixture.drafts.get(),
        )
        assertEquals(
            listOf<AppointmentCreateDestination>(AppointmentCreateDestination.Back),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `plays success haptic when appointment is created`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAppointment(any()) } returns Appointment.stub()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.SuccessHaptic>()
    }

    @Test
    fun `does not play success haptic when appointment creation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAppointment(any()) } throws TestException()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        assertTrue(
            sut.uiState.notifications.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
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
        everySuspend { fixture.createAppointment(any()) } throws
            CreateAppointment.Error.AppointmentOverlap()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `explains employee requirement when caller is not an employee`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAppointment(any()) } throws
            CreateAppointment.Error.NotAnEmployee()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        val message = sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertEquals(
            AppointmentsRes.strings.appointments_create_not_employee_error.desc(),
            message.message,
        )
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `marks date field invalid when date is not allowed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createAppointment(any()) } throws
            CreateAppointment.Error.DateIsNotAllowed()
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
        everySuspend { fixture.createAppointment(any()) } throws
            CreateAppointment.Error.TimeIsNotAllowed()
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
        everySuspend { fixture.createAppointment(any()) } throws
            CreateAppointment.Error.TimeIsNotAllowed()
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

    @Test
    fun `explains appointment creation failures`() = runUnitTest {
        listOf(
            CreateAppointment.Error.PriceChanged() to
                AppointmentsRes.strings.appointments_create_price_changed_error,
            CreateAppointment.Error.DurationChanged() to
                AppointmentsRes.strings.appointments_create_duration_changed_error,
            CreateAppointment.Error.ServicesChanged() to
                AppointmentsRes.strings.appointments_create_services_changed_error,
            CreateAppointment.Error.ServiceNotFound() to
                AppointmentsRes.strings.appointments_complete_service_not_found_error,
            CreateAppointment.Error.EmptyServices() to
                AppointmentsRes.strings.appointments_create_empty_services_error,
            CreateAppointment.Error.DateInPast() to
                AppointmentsRes.strings.appointments_date_in_past_error,
            CreateAppointment.Error.OfferAlreadyUsed() to
                AppointmentsRes.strings.appointments_create_offer_used_error,
            CreateAppointment.Error.EmployeeNotFound() to
                AppointmentsRes.strings.appointments_employee_not_found_error,
            CreateAppointment.Error.ClientNotFound() to
                AppointmentsRes.strings.appointments_client_not_found_error,
            CreateAppointment.Error.EmployeeSuspended() to
                AppointmentsRes.strings.appointments_details_employee_suspended_error,
        ).forEach { (error, expected) ->
            given()
            val fixture = Fixture()
            everySuspend { fixture.createAppointment(any()) } throws error
            val sut = fixture.sut()
            sut.fillValidForm()

            whenn()
            sut.uiState.create.onClick?.invoke()

            then()
            assertEquals(
                listOf(expected.desc()),
                sut.uiState.notifications.presentationNotification
                    .filterIsInstance<PresentationNotification.Message>()
                    .map { it.message },
            )
            assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
            assertTrue(
                sut.uiState.notifications.presentationNotification.none {
                    it is PresentationNotification.SuccessHaptic
                },
            )
        }
    }
}
