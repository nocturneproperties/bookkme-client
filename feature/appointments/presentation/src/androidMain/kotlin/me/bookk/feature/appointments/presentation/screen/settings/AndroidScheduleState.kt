package me.bookk.feature.appointments.presentation.screen.settings

internal class AndroidScheduleState: ScheduleState {
    override val monday: DaySettingsState = AndroidDaySettingsState()
    override val tuesday: DaySettingsState = AndroidDaySettingsState()
    override val wednesday: DaySettingsState = AndroidDaySettingsState()
    override val thursday: DaySettingsState = AndroidDaySettingsState()
    override val friday: DaySettingsState = AndroidDaySettingsState()
    override val saturday: DaySettingsState = AndroidDaySettingsState()
    override val sunday: DaySettingsState = AndroidDaySettingsState()

    override fun asList(): List<DaySettingsState> {
        return listOf(
            monday,
            tuesday,
            wednesday,
            thursday,
            friday,
            saturday,
            sunday
        )
    }
}