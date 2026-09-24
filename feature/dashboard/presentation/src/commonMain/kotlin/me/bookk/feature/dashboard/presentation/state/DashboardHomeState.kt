package me.bookk.feature.dashboard.presentation.state

interface DashboardHomeState {
    var content: HomeContent?
    val onboarding: OnboardingState
}

sealed class HomeContent {
    data object Onboarding : HomeContent()
    data object ActivePlugin : HomeContent()
}

interface OnboardingState {
    var isBusinessStepDone: Boolean
    var isPluginsStepUnlocked: Boolean
    var onCreateBusinessClick: (() -> Unit)?
    var onEnablePluginsClick: (() -> Unit)?
}
