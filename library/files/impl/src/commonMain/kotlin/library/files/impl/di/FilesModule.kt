package library.files.impl.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformFilesModule(): Module

fun filesModule() = module {
    includes(platformFilesModule())
}