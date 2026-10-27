package me.bookk.feature.business.presentation.screen.analytics

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.screen.analytics.BusinessAnalyticsDestinations.Back

class BusinessAnalyticsViewModel(
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: BusinessAnalyticsState = stateFactory.createBusinessAnalyticsState().setup()

    private fun BusinessAnalyticsState.setup() = apply {
        appBar.size = TopBarSize.SMALL
        appBar.title = BusinessRes.strings.business_dashboard_analytics.desc()
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(Back) }
        title = BusinessRes.strings.business_analytics_coming_soon_title.desc()
        message = BusinessRes.strings.business_analytics_coming_soon_message.desc()
    }
}
