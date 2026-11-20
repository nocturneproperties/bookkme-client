package library.files.impl.di

import library.files.api.FileProvider
import library.files.impl.JsFileProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformFilesModule(): Module = module {
    singleOf(::JsFileProvider) bind FileProvider::class
}
