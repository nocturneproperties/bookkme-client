import shared

@MainActor
@Observable
final class IOSScheduleState: @MainActor ScheduleState, NativeStateRepresentation {

    typealias SwiftType = IOSScheduleState
    typealias KotlinType = ScheduleState

    let monday: any DaySettingsState
    let tuesday: any DaySettingsState
    let wednesday: any DaySettingsState
    let thursday: any DaySettingsState
    let friday: any DaySettingsState
    let saturday: any DaySettingsState
    let sunday: any DaySettingsState

    init() {
        monday = IOSDaySettingsState()
        tuesday = IOSDaySettingsState()
        wednesday = IOSDaySettingsState()
        thursday = IOSDaySettingsState()
        friday = IOSDaySettingsState()
        saturday = IOSDaySettingsState()
        sunday = IOSDaySettingsState()
    }

    func asList() -> [any DaySettingsState] {
        return [monday, tuesday, wednesday, thursday, friday, saturday, sunday]
    }
}
