package library.credentials.impl.di

import library.credentials.api.CredentialManager
import library.credentials.api.PasskeyCredentialUpdater
import library.credentials.impl.AndroidCredentialManager
import library.credentials.impl.AndroidPasskeyCredentialUpdater
import me.bookk.core.android.AndroidActivityAware
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformCredentialsModule(): Module = module {
    single { get<CredentialManager>() as AndroidCredentialManager } bind AndroidActivityAware::class
    single { get<PasskeyCredentialUpdater>() as AndroidPasskeyCredentialUpdater } bind AndroidActivityAware::class
}