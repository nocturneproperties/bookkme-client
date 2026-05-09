package me.bookk.database.di

import me.bookk.database.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun databaseBuilderModule(): Module

fun databaseModule() = module {
    includes(databaseBuilderModule())
    includes(daoModule())
    single { AppDatabase.create(get()) }
}

internal fun daoModule() = module {
    factory { get<AppDatabase>().profileDao() }
    factory { get<AppDatabase>().businessDao() }
    factory { get<AppDatabase>().clientDao() }
}