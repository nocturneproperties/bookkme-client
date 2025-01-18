package me.bookk.di

import me.bookk.core.storage.FileProvider
import me.bookk.shared.data.FileProviderImpl
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun platformModule(): Module = module {
    factory<FileProvider> { FileProviderImpl() }
}