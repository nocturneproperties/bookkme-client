package me.bookk.feature.appointments.presentation.screen.requestlist

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.datetime.LocalDate
import me.bookk.designsystem.theme.AppTheme
import me.bookk.feature.appointments.domain.api.entity.Appointment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Monday 2020-01-06 — fixed past week so tests are date-independent.
private val MONDAY = LocalDate(2020, 1, 6)

@RunWith(AndroidJUnit4::class)
class AppointmentListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun setScreen(state: AppointmentListState) = composeTestRule.setContent {
        AppTheme { AppointmentListScreen(state) }
    }

    // region Date strip

    @Test
    fun dateStrip_showsAllSevenDayNumbers() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        setScreen(state)

        // Week of 2020-01-06: Mon 6 … Sun 12
        (6..12).forEach { day ->
            composeTestRule.onNodeWithText(day.toString()).assertIsDisplayed()
        }
    }

    @Test
    fun dateStrip_showsDayInitials() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        setScreen(state)

        listOf("M", "T", "W", "T", "F", "S", "S").forEach { initial ->

            composeTestRule.onNodeWithText(initial).assertIsDisplayed()
        }
    }

    @Test
    fun dateStrip_clickingDay_updatesSelectedDate() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        setScreen(state)

        composeTestRule.onNodeWithText("8").performClick() // Wednesday Jan 8

        assertEquals(LocalDate(2020, 1, 8), state.selectedDate)
    }

    @Test
    fun dateStrip_selectedDateChanges_rendersNewWeek() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        setScreen(state)

        state.selectedDate = LocalDate(2020, 1, 13) // next Monday

        composeTestRule.onNodeWithText("13").assertIsDisplayed()
        composeTestRule.onNodeWithText("19").assertIsDisplayed()
    }

    // endregion

    // region Appointment items

    @Test
    fun appointmentItem_displaysClientName() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        state.appointments.replace(listOf(stubItem(clientName = "Alice Smith")))
        setScreen(state)

        composeTestRule.onNodeWithText("Alice Smith").assertIsDisplayed()
    }

    @Test
    fun appointmentItem_displaysServiceName() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        state.appointments.replace(listOf(stubItem(serviceName = "Haircut")))
        setScreen(state)

        composeTestRule.onNodeWithText("Haircut").assertIsDisplayed()
    }

    @Test
    fun appointmentItem_displaysScheduledTime() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        state.appointments.replace(listOf(stubItem(scheduledAt = "10:30")))
        setScreen(state)

        composeTestRule.onNodeWithText("10:30").assertIsDisplayed()
    }

    @Test
    fun appointmentItem_displaysEarnings() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        state.appointments.replace(listOf(stubItem(earnings = "$75")))
        setScreen(state)

        composeTestRule.onNodeWithText($$"$75").assertIsDisplayed()
    }

    @Test
    fun appointmentList_showsAllItems() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        state.appointments.replace(listOf(
            stubItem(clientName = "Alice Smith"),
            stubItem(clientName = "Bob Jones"),
            stubItem(clientName = "Carol White"),
        ))
        setScreen(state)

        composeTestRule.onNodeWithText("Alice Smith").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bob Jones").assertIsDisplayed()
        composeTestRule.onNodeWithText("Carol White").assertIsDisplayed()
    }

    @Test
    fun appointmentList_empty_showsNoItems() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        state.appointments.replace(emptyList())
        setScreen(state)

        composeTestRule.onNodeWithText("Test Client").assertDoesNotExist()
    }

    // endregion

    // region Interaction

    @Test
    fun appointmentItem_click_invokesCallback() {
        var clicked = false
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        state.appointments.replace(listOf(stubItem(clientName = "Alice Smith", onItemClick = { clicked = true })))
        setScreen(state)

        composeTestRule.onNodeWithText("Alice Smith").performClick()

        assertTrue(clicked)
    }

    @Test
    fun appointmentList_eachItemClick_firesItsOwnCallback() {
        var lastClicked = ""
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        state.appointments.replace(listOf(
            stubItem(clientName = "Alice Smith", onItemClick = { lastClicked = "alice" }),
            stubItem(clientName = "Bob Jones", onItemClick = { lastClicked = "bob" }),
        ))
        setScreen(state)

        composeTestRule.onNodeWithText("Bob Jones").performClick()
        assertEquals("bob", lastClicked)

        composeTestRule.onNodeWithText("Alice Smith").performClick()
        assertEquals("alice", lastClicked)
    }

    // endregion

    // region Date picker

    @Test
    fun datePicker_hiddenByDefault() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        setScreen(state)

        composeTestRule.onNodeWithText("Select").assertDoesNotExist()
        composeTestRule.onNodeWithText("Cancel").assertDoesNotExist()
    }

    @Test
    fun datePicker_showsWhenVisible() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        state.isDatePickerVisible = true
        setScreen(state)

        composeTestRule.onNodeWithText("Select").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun datePicker_cancel_hidesDialog() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        state.isDatePickerVisible = true
        setScreen(state)

        composeTestRule.onNodeWithText("Cancel").performClick()

        assertFalse(state.isDatePickerVisible)
    }

    @Test
    fun datePicker_confirm_callsOnDateSelected() {
        var pickedDate: LocalDate? = null
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        state.onDateSelected = { pickedDate = it }
        state.isDatePickerVisible = true
        setScreen(state)

        composeTestRule.onNodeWithText("Select").performClick()

        assertNotNull(pickedDate)
        assertEquals(MONDAY, pickedDate)
    }

    @Test
    fun datePicker_confirm_hidesDialog() {
        val state = AndroidAppointmentListState(selectedDate = MONDAY)
        state.isDatePickerVisible = true
        setScreen(state)

        composeTestRule.onNodeWithText("Select").performClick()

        assertFalse(state.isDatePickerVisible)
    }

    // endregion

    // region Helpers

    private fun stubItem(
        clientName: String = "Test Client",
        serviceName: String = "Test Service",
        scheduledAt: String = "09:00",
        earnings: String = "$50",
        onItemClick: () -> Unit = {},
    ) = AppointmentItemState(
        clientName = clientName,
        serviceName = serviceName,
        scheduledAt = scheduledAt,
        earnings = earnings,
        source = Appointment.stub(),
        onItemClick = onItemClick,
    )

    // endregion
}
