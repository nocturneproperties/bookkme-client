package me.bookk.feature.business.presentation.screen.settings

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
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import library.device.api.DeviceFacade
import library.money.api.Currency
import library.money.api.Money
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.business.UpdateBusiness
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.presentation.FakeBusinessStateFactory
import me.bookk.feature.business.presentation.screen.settings.state.BusinessSettingsDestination
import me.bookk.feature.business.presentation.stubBusiness
import me.bookk.feature.business.presentation.stubPermissions
import me.bookk.feature.business.presentation.stubWorkingSchedule
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BusinessSettingsViewModelTest {

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
        val business = MutableStateFlow<Business?>(null)
        val deviceFacade = mock<DeviceFacade> {
            every { openMapAt(any(), any()) } returns Unit
        }
        val updateBusiness = mock<UpdateBusiness>()
        val observeDashboardBusinessChanges = mock<ObserveDashboardBusinessChanges> {
            every { invoke() } returns business
        }
        val errorMapper = FakeErrorMapper()
        val updates = Capture.slot<Business.Update>()

        fun sut() = BusinessSettingsViewModel(
            deviceFacade = deviceFacade,
            updateBusiness = updateBusiness,
            observeDashboardBusinessChanges = observeDashboardBusinessChanges,
            dateLocalizer = FakeDateLocalizer(),
            stateFactory = FakeBusinessStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )

        fun sutWith(business: Business): BusinessSettingsViewModel {
            this.business.value = business
            return sut()
        }

        fun stubUpdateEcho() {
            everySuspend { updateBusiness(capture(updates)) } calls { (update: Business.Update) ->
                stubBusiness(id = update.id, name = update.name, schedule = update.schedule)
            }
        }
    }

    private fun socialBusiness() = stubBusiness(
        name = "Salon",
        socials = mapOf(
            Business.SocialKind.INSTAGRAM to Business.Social(Business.SocialKind.INSTAGRAM, "insta"),
            Business.SocialKind.PHONE to Business.Social(Business.SocialKind.PHONE, "+380")
        )
    )

    @Test
    fun `renders business details`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(socialBusiness())

        then()
        assertEquals("Salon", sut.uiState.name.text)
        assertEquals("insta", sut.uiState.instagram.text)
        assertEquals("+380", sut.uiState.phone.text)
        assertEquals("", sut.uiState.telegram.text)
        assertEquals(Money.SupportedCurrency.USD, sut.uiState.currency.selectedItem?.domainValue)
    }

    @Test
    fun `renders seven schedule days`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(stubBusiness())

        then()
        assertEquals(7, sut.uiState.schedule.list.items.size)
    }

    @Test
    fun `marks blank name invalid and disables save`() = runUnitTest {
        given()
        val sut = Fixture().sutWith(stubBusiness())

        whenn()
        sut.onNameChanged("   ")

        then()
        assertFalse(sut.uiState.name.isValid)
        assertEquals(ValidationState.ERROR, sut.uiState.name.validationState)
        assertFalse(sut.uiState.save.isEnabled)
    }

    @Test
    fun `enables save when name changes`() = runUnitTest {
        given()
        val sut = Fixture().sutWith(stubBusiness(name = "Old"))

        whenn()
        sut.onNameChanged("New")

        then()
        assertTrue(sut.uiState.save.isEnabled)
    }

    @Test
    fun `disables save when name is changed back to the original`() = runUnitTest {
        given()
        val sut = Fixture().sutWith(stubBusiness(name = "Old"))
        sut.onNameChanged("New")

        whenn()
        sut.onNameChanged("Old")

        then()
        assertFalse(sut.uiState.save.isEnabled)
    }

    @Test
    fun `keeps only digits and leading plus in phone`() = runUnitTest {
        given()
        val sut = Fixture().sutWith(stubBusiness())

        whenn()
        sut.onPhoneChanged("+38 (050) 1")

        then()
        assertEquals("+380501", sut.uiState.phone.text)
    }

    @Test
    fun `saves trimmed fields and socials`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubUpdateEcho()
        val sut = fixture.sutWith(stubBusiness(name = "Old"))
        sut.onNameChanged("New ")
        sut.onDescriptionChanged("  About us  ")
        sut.onInstagramChanged("insta")

        whenn()
        sut.onSaveClick()

        then()
        val update = fixture.updates.get()
        assertEquals("New", update.name)
        assertEquals("About us", update.description)
        assertEquals("insta", update.socials[Business.SocialKind.INSTAGRAM]?.value)
        assertEquals(Currency("USD"), update.currency)
    }

    @Test
    fun `shows success message and stops loading after save`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubUpdateEcho()
        val sut = fixture.sutWith(stubBusiness())

        whenn()
        sut.onSaveClick()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.GlobalMessage>()
        assertFalse(sut.uiState.save.isLoading)
    }

    @Test
    fun `saves changed location`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubUpdateEcho()
        val sut = fixture.sutWith(stubBusiness())
        sut.onLocationChanged(50.0, 30.0)

        whenn()
        sut.onSaveClick()

        then()
        assertEquals("50.0, 30.0", sut.uiState.location.text)
        assertEquals(Business.Location(50.0, 30.0), fixture.updates.get().location)
    }

    @Test
    fun `saves deactivated day`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubUpdateEcho()
        val sut = fixture.sutWith(stubBusiness())
        sut.uiState.schedule.monday.isActive.onCheckedChange?.invoke(false)

        whenn()
        sut.onSaveClick()

        then()
        assertFalse(fixture.updates.get().schedule.monday.isActive)
    }

    @Test
    fun `enables save after toggling a day`() = runUnitTest {
        given()
        val sut = Fixture().sutWith(stubBusiness())
        val initial = sut.uiState.schedule.monday.isActive.isChecked

        whenn()
        sut.uiState.schedule.monday.isActive.onCheckedChange?.invoke(!initial)

        then()
        assertTrue(sut.uiState.save.isEnabled)
    }

    @Test
    fun `adds empty interval on add time click`() = runUnitTest {
        given()
        val sut = Fixture().sutWith(stubBusiness())
        val before = sut.uiState.schedule.monday.intervals.size

        whenn()
        sut.uiState.schedule.monday.addTimeButton.onClick?.invoke()

        then()
        assertEquals(before + 1, sut.uiState.schedule.monday.intervals.size)
    }

    @Test
    fun `saves picked working time of a new interval`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubUpdateEcho()
        val schedule = stubWorkingSchedule().let {
            it.copy(days = it.days + (DayOfWeek.MONDAY to it.monday.copy(workingTime = emptyList(), isActive = true)))
        }
        val sut = fixture.sutWith(stubBusiness(schedule = schedule))
        sut.uiState.schedule.monday.addTimeButton.onClick?.invoke()
        val interval = sut.uiState.schedule.monday.intervals.single()
        interval.timeFromPicker.timePicker.onTimePicked?.invoke(LocalTime(10, 0))
        interval.timeToPicker.timePicker.onTimePicked?.invoke(LocalTime(18, 0))

        whenn()
        sut.onSaveClick()

        then()
        assertEquals(listOf(WorkHour(LocalTime(10, 0), LocalTime(18, 0))), fixture.updates.get().schedule.monday.workingTime)
    }

    @Test
    fun `removes interval on delete`() = runUnitTest {
        given()
        val sut = Fixture().sutWith(stubBusiness())
        sut.uiState.schedule.monday.addTimeButton.onClick?.invoke()
        val added = sut.uiState.schedule.monday.intervals.last()
        val before = sut.uiState.schedule.monday.intervals.size

        whenn()
        sut.uiState.schedule.monday.onDeleteInterval(added)

        then()
        assertEquals(before - 1, sut.uiState.schedule.monday.intervals.size)
    }

    @Test
    fun `adds picked date range as day off and resets pickers`() = runUnitTest {
        given()
        val sut = Fixture().sutWith(stubBusiness())
        val start = LocalDate(2030, 1, 10)
        val end = LocalDate(2030, 1, 12)
        sut.uiState.schedule.dateRange.startDate.datePicker.onDatePicked?.invoke(start)
        sut.uiState.schedule.dateRange.endDate.datePicker.onDatePicked?.invoke(end)

        whenn()
        sut.uiState.schedule.dateRange.onDateRangeSelected()

        then()
        val dayOff = sut.uiState.schedule.dayOffs.selectedItems.single()
        assertEquals(start, dayOff.dateFrom)
        assertEquals(end, dayOff.dateTo)
        assertEquals(null, sut.uiState.schedule.dateRange.startDate.datePicker.pickedDate)
        assertEquals(null, sut.uiState.schedule.dateRange.endDate.datePicker.pickedDate)
        assertTrue(sut.uiState.save.isEnabled)
    }

    @Test
    fun `limits end date to picked start date`() = runUnitTest {
        given()
        val sut = Fixture().sutWith(stubBusiness())
        val start = LocalDate(2030, 1, 10)

        whenn()
        sut.uiState.schedule.dateRange.startDate.datePicker.onDatePicked?.invoke(start)

        then()
        assertEquals(start, sut.uiState.schedule.dateRange.endDate.datePicker.minDate)
    }

    @Test
    fun `ignores date range selection without end date`() = runUnitTest {
        given()
        val sut = Fixture().sutWith(stubBusiness())
        sut.uiState.schedule.dateRange.startDate.datePicker.onDatePicked?.invoke(LocalDate(2030, 1, 10))

        whenn()
        sut.uiState.schedule.dateRange.onDateRangeSelected()

        then()
        assertTrue(sut.uiState.schedule.dayOffs.selectedItems.isEmpty())
    }

    @Test
    fun `removes day off`() = runUnitTest {
        given()
        val dayOff = DayOffRange(LocalDate(2030, 1, 10), LocalDate(2030, 1, 12))
        val sut = Fixture().sutWith(stubBusiness(schedule = stubWorkingSchedule().copy(dayOffs = listOf(dayOff))))
        val rendered = sut.uiState.schedule.dayOffs.selectedItems.toList()

        whenn()
        sut.uiState.schedule.dayOffs.onItemsRemoveRequested(rendered)

        then()
        assertTrue(sut.uiState.schedule.dayOffs.selectedItems.isEmpty())
        assertTrue(sut.uiState.save.isEnabled)
    }

    @Test
    fun `shows message when active day has no work hours`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateBusiness(any()) } throws UpdateBusiness.Error.ActiveDayWithoutWorkHours()
        val sut = fixture.sutWith(stubBusiness())

        whenn()
        sut.onSaveClick()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows message when day off range is invalid`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateBusiness(any()) } throws UpdateBusiness.Error.InvalidDayOffRange()
        val sut = fixture.sutWith(stubBusiness())

        whenn()
        sut.onSaveClick()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows mapped error when save fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateBusiness(any()) } throws TestException()
        val sut = fixture.sutWith(stubBusiness())

        whenn()
        sut.onSaveClick()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.save.isLoading)
    }

    @Test
    fun `opens map at business location`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(stubBusiness())
        sut.onLocationChanged(50.0, 30.0)

        whenn()
        sut.onTestLocationClick()

        then()
        verify { fixture.deviceFacade.openMapAt(50.0, 30.0) }
    }

    @Test
    fun `does not open map without location`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(stubBusiness())

        whenn()
        sut.onTestLocationClick()

        then()
        verify(VerifyMode.not) { fixture.deviceFacade.openMapAt(any(), any()) }
    }

    @Test
    fun `shows mapped error when business observation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeDashboardBusinessChanges() } returns failOnceThenSuspend()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    private fun BusinessSettingsViewModel.editableFields() = with(uiState) {
        listOf(
            name.enabled,
            description.enabled,
            address.enabled,
            phone.enabled,
            instagram.enabled,
            telegram.enabled,
            viber.enabled,
            currency.textField.enabled,
            photo.isEnabled,
            schedule.isEditable
        )
    }

    private fun editableBusiness() = stubBusiness(
        permissions = stubPermissions(business = ResourcePermission(view = true, update = true))
    )

    private fun readOnlyBusiness() = stubBusiness(
        permissions = stubPermissions(business = ResourcePermission(view = true))
    )

    @Test
    fun `hides save while the business is loading`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.save.isVisible)
    }

    @Test
    fun `shows save and unlocks every field with business edit permission`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(editableBusiness())

        then()
        assertTrue(sut.uiState.save.isVisible)
        assertTrue(sut.editableFields().all { it })
    }

    @Test
    fun `hides save and locks every field without business edit permission`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sutWith(readOnlyBusiness())

        then()
        assertFalse(sut.uiState.save.isVisible)
        assertTrue(sut.editableFields().none { it })
    }

    @Test
    fun `locks every field when business edit permission is revoked`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sutWith(editableBusiness())

        whenn()
        fixture.business.value = readOnlyBusiness()

        then()
        assertFalse(sut.uiState.save.isVisible)
        assertTrue(sut.editableFields().none { it })
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sutWith(stubBusiness())

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertNotNull(sut.uiState.navigation.navigationDestination.singleOrNull { it == BusinessSettingsDestination.Back })
    }
}
