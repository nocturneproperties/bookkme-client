package me.bookk.feature.authorization.domain.impl.di

import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.api.FinishSignUp
import me.bookk.feature.authorization.domain.api.GetSettingsColorScheme
import me.bookk.feature.authorization.domain.api.GetSignUpLegalDocuments
import me.bookk.feature.authorization.domain.api.GetTokenInfo
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.api.InitiateBusinessSuspend
import me.bookk.feature.authorization.domain.api.IsUserLoggedIn
import me.bookk.feature.authorization.domain.api.LogOut
import me.bookk.feature.authorization.domain.api.ObserveSignUpRejection
import me.bookk.feature.authorization.domain.api.RefreshToken
import me.bookk.feature.authorization.domain.api.ResendSignUpCode
import me.bookk.feature.authorization.domain.api.SignIn
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.impl.ConfirmSensitiveActionImpl
import me.bookk.feature.authorization.domain.impl.FinishSignUpImpl
import me.bookk.feature.authorization.domain.impl.GetSettingsColorSchemeImpl
import me.bookk.feature.authorization.domain.impl.GetSignUpLegalDocumentsImpl
import me.bookk.feature.authorization.domain.impl.GetTokenInfoImpl
import me.bookk.feature.authorization.domain.impl.InitialAppDataFetchImpl
import me.bookk.feature.authorization.domain.impl.InitiateBusinessSuspendImpl
import me.bookk.feature.authorization.domain.impl.IsUserLoggedInImpl
import me.bookk.feature.authorization.domain.impl.LogOutImpl
import me.bookk.feature.authorization.domain.impl.LowPriorityDataFetch
import me.bookk.feature.authorization.domain.impl.ObserveSignUpRejectionImpl
import me.bookk.feature.authorization.domain.impl.RefreshTokenImpl
import me.bookk.feature.authorization.domain.impl.ResendSignUpCodeImpl
import me.bookk.feature.authorization.domain.impl.SignInImpl
import me.bookk.feature.authorization.domain.impl.StartSignUpImpl
import me.bookk.feature.authorization.domain.impl.UserProfileCRUDImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Clock

fun authDomainModule() = module {
    factoryOf(::ConfirmSensitiveActionImpl) bind ConfirmSensitiveAction::class
    single<Clock> { Clock.System }
    factoryOf(::GetSignUpLegalDocumentsImpl) bind GetSignUpLegalDocuments::class
    factoryOf(::StartSignUpImpl) bind StartSignUp::class
    factoryOf(::FinishSignUpImpl) bind FinishSignUp::class
    factoryOf(::ResendSignUpCodeImpl) bind ResendSignUpCode::class
    factoryOf(::ObserveSignUpRejectionImpl) bind ObserveSignUpRejection::class
    factoryOf(::GetTokenInfoImpl) bind GetTokenInfo::class
    factoryOf(::RefreshTokenImpl) bind RefreshToken::class
    factoryOf(::SignInImpl) bind SignIn::class
    factoryOf(::IsUserLoggedInImpl) bind IsUserLoggedIn::class
    factoryOf(::UserProfileCRUDImpl) bind UserProfileCRUD::class
    factoryOf(::InitialAppDataFetchImpl) bind InitialAppDataFetch::class
    factory { LogOutImpl(getAll()) } bind LogOut::class
    factoryOf(::InitiateBusinessSuspendImpl) bind InitiateBusinessSuspend::class
    factoryOf(::GetSettingsColorSchemeImpl) bind GetSettingsColorScheme::class
    factoryOf(::LowPriorityDataFetch)
}
