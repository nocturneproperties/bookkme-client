package library.validation.impl.di

import library.validation.api.ValidateEmail
import library.validation.api.ValidateName
import library.validation.impl.ValidateEmailImpl
import library.validation.impl.ValidateNameImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

fun validationModule() = module {
    factoryOf<ValidateName>(::ValidateNameImpl)
    factoryOf<ValidateEmail>(::ValidateEmailImpl)
}