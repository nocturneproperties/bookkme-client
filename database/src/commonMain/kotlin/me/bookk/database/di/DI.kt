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

fun daoModule() = module {

}