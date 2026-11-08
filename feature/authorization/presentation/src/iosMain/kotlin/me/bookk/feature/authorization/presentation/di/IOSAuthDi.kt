package me.bookk.feature.authorization.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.authorization.domain.api.StartEmailChange
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapViewModel
import me.bookk.feature.authorization.presentation.screen.changeemail.ChangeEmailViewModel
import me.bookk.feature.authorization.presentation.screen.verifyemail.VerifyEmailRequest
import me.bookk.feature.authorization.presentation.screen.verifyemail.VerifyEmailViewModel
import me.bookk.feature.authorization.presentation.sign_in.SignInViewModel
import me.bookk.feature.authorization.presentation.sign_up.SignUpViewModel
import me.bookk.feature.authorization.presentation.troubleshoot.TroubleshootViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal actual fun platformAuthDiModule(): Module = module {
    factoryOf(::SignUpViewModel)
    factoryOf(::SignInViewModel)
    factoryOf(::BootstrapViewModel)
    factoryOf(::TroubleshootViewModel)
    factoryOf(::VerifyEmailViewModel)
    factoryOf(::ChangeEmailViewModel)
}

@UsedInSwift
fun signUpVM(): SignUpViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun signInVM(): SignInViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun bootstrapVM(): BootstrapViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun troubleshootVM(): TroubleshootViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun verifyEmailVM(pendingSignUp: StartSignUp.PendingSignUp): VerifyEmailViewModel =
    KoinPlatform.getKoin().get(parameters = {
        parametersOf(VerifyEmailRequest.SignUp(pendingSignUp))
    })

@UsedInSwift
fun verifyEmailChangeVM(
    pendingEmailChange: StartEmailChange.PendingEmailChange,
): VerifyEmailViewModel = KoinPlatform.getKoin().get(parameters = {
    parametersOf(VerifyEmailRequest.EmailChange(pendingEmailChange))
})

@UsedInSwift
fun changeEmailVM(): ChangeEmailViewModel = KoinPlatform.getKoin().get()
