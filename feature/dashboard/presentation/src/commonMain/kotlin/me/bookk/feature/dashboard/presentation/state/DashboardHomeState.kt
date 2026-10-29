package me.bookk.feature.dashboard.presentation.state

import dev.icerock.moko.resources.desc.StringDesc
import kotlin.uuid.Uuid

interface DashboardHomeState {
    var content: HomeContent?
    val onboarding: OnboardingState
}

sealed class HomeContent {
    data object NoBusiness : HomeContent()
    data object SetupRequired : HomeContent()
    data object AwaitingSetup : HomeContent()
    data object ActivePlugin : HomeContent()
}

interface OnboardingState {
    var awaitingSetupMessage: StringDesc
    var onCreateBusinessClick: (() -> Unit)?
    var onJoinBusinessClick: (() -> Unit)?
    var onEnablePluginsClick: (() -> Unit)?
    var onAddServiceGroupClick: (() -> Unit)?
    var onAddServiceClick: (() -> Unit)?
    var onAddClientClick: (() -> Unit)?
    var isAppointmentsStepDone: Boolean
    var isServiceGroupStepDone: Boolean
    var isServiceStepDone: Boolean
    var isServiceStepEnabled: Boolean
    var isAppointmentsStepEnabled: Boolean
    var isServiceGroupStepEnabled: Boolean
    var isClientStepEnabled: Boolean
    var serviceGroupStepSubtitle: StringDesc
    var serviceStepSubtitle: StringDesc
    var clientStepSubtitle: StringDesc
    var isClientStepDone: Boolean
    var businesses: List<OnboardingBusinessItem>
    var onBusinessClick: ((OnboardingBusinessItem) -> Unit)?
}

data class OnboardingBusinessItem(
    val id: Uuid,
    val name: String
)
