package me.bookk.feature.authorization.domain.impl.di

import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.api.GetSettingsColorScheme
import me.bookk.feature.authorization.domain.api.GetTokenInfo
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.api.IsUserLoggedIn
import me.bookk.feature.authorization.domain.api.LogOut
import me.bookk.feature.authorization.domain.api.RefreshToken
import me.bookk.feature.authorization.domain.api.SignIn
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.impl.CreateAccountImpl
import me.bookk.feature.authorization.domain.impl.GetSettingsColorSchemeImpl
import me.bookk.feature.authorization.domain.impl.GetTokenInfoImpl
import me.bookk.feature.authorization.domain.impl.InitialAppDataFetchImpl
import me.bookk.feature.authorization.domain.impl.IsUserLoggedInImpl
import me.bookk.feature.authorization.domain.impl.LogOutImpl
import me.bookk.feature.authorization.domain.impl.RefreshTokenImpl
import me.bookk.feature.authorization.domain.impl.SignInImpl
import me.bookk.feature.authorization.domain.impl.UserProfileCRUDImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun authDomainModule() = module {
    factoryOf(::CreateAccountImpl) bind CreateAccount::class
    factoryOf(::GetTokenInfoImpl) bind GetTokenInfo::class
    factoryOf(::RefreshTokenImpl) bind RefreshToken::class
    factoryOf(::SignInImpl) bind SignIn::class
    factoryOf(::IsUserLoggedInImpl) bind IsUserLoggedIn::class
    factoryOf(::UserProfileCRUDImpl) bind UserProfileCRUD::class
    factoryOf(::InitialAppDataFetchImpl) bind InitialAppDataFetch::class
    factory { LogOutImpl(getAll()) } bind LogOut::class
    factoryOf(::GetSettingsColorSchemeImpl) bind GetSettingsColorScheme::class
}