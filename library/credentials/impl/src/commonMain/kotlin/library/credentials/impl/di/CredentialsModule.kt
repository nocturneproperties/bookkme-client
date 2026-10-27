package library.credentials.impl.di

import library.credentials.di.CredentialModuleFactory
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformCredentialsModule(): Module

fun credentialsModule() = module {
    single { get<CredentialModuleFactory>().createCredentialManager() }
    includes(platformCredentialsModule())
}