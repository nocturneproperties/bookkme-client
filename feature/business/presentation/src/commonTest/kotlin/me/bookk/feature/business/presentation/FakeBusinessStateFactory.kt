package me.bookk.feature.business.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.test.FakeAppBarState
import me.bookk.designsystem.test.FakeBusinessMenuState
import me.bookk.designsystem.test.FakeButtonState
import me.bookk.designsystem.test.FakeListState
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.designsystem.test.FakePickerFieldState
import me.bookk.designsystem.test.FakeScheduleState
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.uistate.simple.Action
import me.bookk.designsystem.uistate.simple.OptionalInfoLine
import me.bookk.feature.business.presentation.screen.create.CreateBusinessNavigationDestination
import me.bookk.feature.business.presentation.screen.create.state.CreateBusinessState
import me.bookk.feature.business.presentation.screen.dashboard.DashboardNavigationDestination
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardSection
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardState
import me.bookk.feature.business.presentation.screen.plugins.BusinessPluginListState
import me.bookk.feature.business.presentation.screen.plugins.BusinessPluginState
import me.bookk.feature.business.presentation.screen.plugins.BusinessPluginsDestinations
import me.bookk.feature.business.presentation.screen.settings.state.BusinessSettingsDestination
import me.bookk.feature.business.presentation.screen.settings.state.BusinessSettingsState
import me.bookk.feature.business.presentation.screen.settings.state.CurrencyUI

internal class FakeBusinessStateFactory : BusinessStateFactory {
    var createBusinessInitData: CreateBusinessState.InitData? = null
    var businessSettingsInitData: BusinessSettingsState.InitData? = null

    override fun createBusinessState(initData: CreateBusinessState.InitData): CreateBusinessState {
        createBusinessInitData = initData
        return FakeCreateBusinessState()
    }

    override fun createBusinessDashboardState(): BusinessDashboardState {
        return FakeBusinessDashboardState()
    }

    override fun createBusinessSettingsState(initData: BusinessSettingsState.InitData): BusinessSettingsState {
        businessSettingsInitData = initData
        return FakeBusinessSettingsState()
    }

    override fun createBusinessPluginListState(): BusinessPluginListState {
        return FakeBusinessPluginListState()
    }

    override fun createBusinessPluginState(): BusinessPluginState {
        return FakeBusinessPluginState()
    }
}

internal class FakeCreateBusinessState : CreateBusinessState {
    override val appBar = FakeAppBarState()
    override val name = FakeTextFieldState()
    override val createBtn = FakeButtonState()
    override val navigation = FakeNavigationState<CreateBusinessNavigationDestination>()
    override val notifications = FakeNotificationState()
}

internal class FakeBusinessDashboardState : BusinessDashboardState {
    override val appBar = FakeAppBarState()
    override val sections: MutableList<BusinessDashboardSection> = mutableListOf()
    override val businessMenu = FakeBusinessMenuState()
    override var isCreateBusinessSheetVisible: Boolean = false
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<DashboardNavigationDestination>()

    override fun updateSections(sections: List<BusinessDashboardSection>) {
        this.sections.clear()
        this.sections += sections
    }
}

internal class FakeBusinessPluginListState : BusinessPluginListState {
    override val appBar = FakeAppBarState()
    override val appointmentPlugin = FakeBusinessPluginState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<BusinessPluginsDestinations>()
}

internal class FakeBusinessPluginState : BusinessPluginState {
    override var title: StringDesc = "".desc()
    override var subtitle: StringDesc = "".desc()
    override var isEnabled: Boolean = false
    override var isExpanded: Boolean = false
    override val youCan = FakeListState<OptionalInfoLine>()
    override val clientCan = FakeListState<OptionalInfoLine>()
    override var demo: Action? = null
    override val enable = FakeButtonState()
}

internal class FakeBusinessSettingsState : BusinessSettingsState {
    override val appBar = FakeAppBarState()
    override val photo = FakeButtonState()
    override val name = FakeTextFieldState()
    override val description = FakeTextFieldState()
    override val location = FakeTextFieldState()
    override val address = FakeTextFieldState()
    override val currency = FakePickerFieldState<CurrencyUI>()
    override val phone = FakeTextFieldState()
    override val instagram = FakeTextFieldState()
    override val telegram = FakeTextFieldState()
    override val viber = FakeTextFieldState()
    override val schedule = FakeScheduleState()
    override val pickLocation = FakeButtonState()
    override val testLocation = FakeButtonState()
    override val save = FakeButtonState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<BusinessSettingsDestination>()
}
