package library.credentials.impl.di

import library.credentials.api.CredentialManager
import library.credentials.impl.AndroidCredentialManager
import me.bookk.core.android.AndroidActivityAware
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

internal actual fun platformCredentialsModule() = module {
    singleOf(::AndroidCredentialManager) binds arrayOf(CredentialManager::class, AndroidActivityAware::class)
}