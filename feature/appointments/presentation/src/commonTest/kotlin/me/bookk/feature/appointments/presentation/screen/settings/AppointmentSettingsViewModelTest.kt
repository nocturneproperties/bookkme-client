package me.bookk.feature.appointments.presentation.screen.settings

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
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.UpdateAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.presentation.FakeAppointmentsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class AppointmentSettingsViewModelTest {

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
        val cached = MutableStateFlow<AppointmentSettings?>(null)
        val getAppointmentSettings = mock<GetAppointmentSettings> {
            every { flow(any()) } returns cached
            everySuspend { refresh(any()) } returns AppointmentSettings.stub(businessId)
        }
        val updateAppointmentSettings = mock<UpdateAppointmentSettings>()
        val errorMapper = FakeErrorMapper()
        val updates = Capture.slot<AppointmentSettings>()

        fun sut() = AppointmentSettingsViewModel(
            businessId = businessId,
            getAppointmentSettings = getAppointmentSettings,
            updateAppointmentSettings = updateAppointmentSettings,
            stateFactory = FakeAppointmentsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `refreshes settings of business on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getAppointmentSettings.refresh(fixture.businessId) }
    }

    @Test
    fun `renders cached settings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = AppointmentSettings.stub(fixture.businessId).copy(
            automaticApproval = true,
            inBetweenBreakInMinutes = 15,
            appointmentNote = "Bring ID"
        )

        then()
        assertTrue(sut.uiState.automaticApproval.isChecked)
        assertEquals("15", sut.uiState.minimalBreak.text)
        assertEquals("Bring ID", sut.uiState.note.text)
    }

    @Test
    fun `keeps only digits in minimal break`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.minimalBreak.onTextChanged?.invoke("1a5")

        then()
        assertEquals("15", sut.uiState.minimalBreak.text)
    }

    @Test
    fun `saves edited settings over the loaded ones`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateAppointmentSettings(capture(fixture.updates)) } calls { (s: AppointmentSettings) -> s }
        val loaded = AppointmentSettings.stub(fixture.businessId)
        val sut = fixture.sut()
        fixture.cached.value = loaded
        sut.uiState.automaticApproval.onCheckedChange?.invoke(true)
        (sut.uiState.minimalBreak as FakeTextFieldState).type("20")
        (sut.uiState.note as FakeTextFieldState).type("Bring ID")

        whenn()
        sut.uiState.save.onClick?.invoke()

        then()
        assertEquals(
            loaded.copy(automaticApproval = true, inBetweenBreakInMinutes = 20, appointmentNote = "Bring ID"),
            fixture.updates.get()
        )
        sut.uiState.notifications.assertSingle<PresentationNotification.GlobalMessage>()
        assertFalse(sut.uiState.save.isLoading)
    }

    @Test
    fun `falls back to ten minute break when break is empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateAppointmentSettings(capture(fixture.updates)) } calls { (s: AppointmentSettings) -> s }
        val sut = fixture.sut()
        (sut.uiState.minimalBreak as FakeTextFieldState).type("")

        whenn()
        sut.uiState.save.onClick?.invoke()

        then()
        assertEquals(10, fixture.updates.get().inBetweenBreakInMinutes)
    }

    @Test
    fun `shows mapped error when save fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateAppointmentSettings(any()) } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.uiState.save.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.save.isLoading)
    }

    @Test
    fun `shows mapped error when refresh fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAppointmentSettings.refresh(any()) } throws TestException()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `shows mapped error when settings observation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getAppointmentSettings.flow(any()) } returns failOnceThenSuspend()

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
        assertEquals(listOf<AppointmentSettingsDestination>(AppointmentSettingsDestination.Back), sut.uiState.navigation.navigationDestination)
    }
}
