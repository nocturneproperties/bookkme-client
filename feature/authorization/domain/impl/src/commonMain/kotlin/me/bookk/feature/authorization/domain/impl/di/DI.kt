package me.bookk.feature.authorization.domain.impl.di

import me.bookk.feature.authorization.domain.api.ValidateEmail
import me.bookk.feature.authorization.domain.api.ValidateName
import me.bookk.feature.authorization.domain.impl.ValidateEmailImpl
import me.bookk.feature.authorization.domain.impl.ValidateNameImpl
import org.koin.dsl.module

fun authDomainModule() = module {
    factory<ValidateName> { ValidateNameImpl() }
    factory<ValidateEmail> { ValidateEmailImpl() }
}