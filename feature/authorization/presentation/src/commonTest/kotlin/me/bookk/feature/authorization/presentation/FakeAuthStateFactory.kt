package me.bookk.feature.authorization.presentation

import me.bookk.designsystem.test.FakeAppBarState
import me.bookk.designsystem.test.FakeButtonState
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapNavigationDestination
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState
import me.bookk.feature.authorization.presentation.shared.PasskeyInfoCardData
import me.bookk.feature.authorization.presentation.sign_in.SignInNavigationDestination
import me.bookk.feature.authorization.presentation.sign_in.state.SignInState
import me.bookk.feature.authorization.presentation.sign_up.SignUpNavigationDestination
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState
import me.bookk.feature.authorization.presentation.sign_up.state.TroubleshootCardData
import me.bookk.feature.authorization.presentation.troubleshoot.TroubleshootNavigationDestination
import me.bookk.feature.authorization.presentation.troubleshoot.state.TroubleshootState

internal class FakeAuthStateFactory : AuthStateFactory {
    var troubleshootInitData: TroubleshootState.InitData? = null

    override fun createSignUpState(initData: SignUpState.InitData): SignUpState = FakeSignUpState(initData.passkeyInfoCardData)
    override fun createSignInState(initData: SignInState.InitData): SignInState = FakeSignInState(initData.passkeyInfoCardData)

    override fun createTroubleshootState(initData: TroubleshootState.InitData): TroubleshootState {
        troubleshootInitData = initData
        return FakeTroubleshootState(initData.cardData)
    }

    override fun createBootstrapState(): BootstrapState = FakeBootstrapState()
}

internal class FakeBootstrapState : BootstrapState {
    override var colorScheme: BootstrapState.UIColorScheme = BootstrapState.UIColorScheme.SYSTEM
    override var startDestination: BootstrapNavigationDestination? = null
}

internal class FakeSignInState(override val passkeyInfoCardData: PasskeyInfoCardData) : SignInState {
    override val appBar = FakeAppBarState()
    override val learnMoreButton = FakeButtonState()
    override val signInButton = FakeButtonState()
    override val signUpButton = FakeButtonState()
    override val troubleshootButton = FakeButtonState()
    override val notification = FakeNotificationState()
    override val navigation = FakeNavigationState<SignInNavigationDestination>()
}

internal class FakeSignUpState(override val passkeyInfoCardData: PasskeyInfoCardData) : SignUpState {
    override val appBar = FakeAppBarState()
    override val name = FakeTextFieldState()
    override val lastName = FakeTextFieldState()
    override val email = FakeTextFieldState()
    override val learnMoreButton = FakeButtonState()
    override val confirmButton = FakeButtonState()
    override val notification = FakeNotificationState()
    override val navigation = FakeNavigationState<SignUpNavigationDestination>()
}

internal class FakeTroubleshootState(override val troubleshootCardStaticData: TroubleshootCardData) : TroubleshootState {
    override val appBar = FakeAppBarState()
    override val contactSupportButton = FakeButtonState()
    override val navigation = FakeNavigationState<TroubleshootNavigationDestination>()
}
