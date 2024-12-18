package me.bookk.database.di

import me.bookk.database.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun databaseBuilderModule(): Module = module {
    factory { getDatabaseBuilder() }
}