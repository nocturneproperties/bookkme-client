package me.bookk.feature.authorization.domain.impl.di

import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.api.GetTokenInfo
import me.bookk.feature.authorization.domain.api.RefreshToken
import me.bookk.feature.authorization.domain.api.SignIn
import me.bookk.feature.authorization.domain.api.ValidateEmail
import me.bookk.feature.authorization.domain.api.ValidateName
import me.bookk.feature.authorization.domain.impl.CreateAccountImpl
import me.bookk.feature.authorization.domain.impl.GetTokenInfoImpl
import me.bookk.feature.authorization.domain.impl.RefreshTokenImpl
import me.bookk.feature.authorization.domain.impl.SignInImpl
import me.bookk.feature.authorization.domain.impl.ValidateEmailImpl
import me.bookk.feature.authorization.domain.impl.ValidateNameImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun authDomainModule() = module {
    factoryOf<ValidateName>(::ValidateNameImpl)
    factoryOf<ValidateEmail>(::ValidateEmailImpl)
    factoryOf(::CreateAccountImpl) bind CreateAccount::class
    factoryOf(::GetTokenInfoImpl) bind GetTokenInfo::class
    factoryOf(::RefreshTokenImpl) bind RefreshToken::class
    factoryOf(::SignInImpl) bind SignIn::class
}