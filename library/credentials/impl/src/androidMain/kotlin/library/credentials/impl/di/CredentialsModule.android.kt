package library.credentials.impl.di

import library.credentials.api.CredentialManager
import library.credentials.impl.AndroidCredentialManager
import me.bookk.core.android.AndroidActivityAware
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformCredentialsModule(): Module = module {
    single { get<CredentialManager>() as AndroidCredentialManager } bind AndroidActivityAware::class
}