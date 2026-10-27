package library.files.impl.di

import library.files.api.FileProvider
import library.files.impl.IosFileProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformFilesModule(): Module = module {
    factoryOf(::IosFileProvider) bind FileProvider::class
}