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
    var businesses: List<OnboardingBusinessItem>
    var onBusinessClick: ((OnboardingBusinessItem) -> Unit)?
}

data class OnboardingBusinessItem(
    val id: Uuid,
    val name: String
)
