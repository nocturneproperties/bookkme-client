package library.credentials.impl.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformCredentialsModule(): Module

fun credentialsModule() = module {
    includes(platformCredentialsModule())
}