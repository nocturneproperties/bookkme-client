package me.bookk.database.business.di

import me.bookk.database.business.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun databaseBuilderModule(): Module = module {
    factory { getDatabaseBuilder() }
}
