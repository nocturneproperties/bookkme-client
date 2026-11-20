package me.bookk.database.client.di

import me.bookk.database.account.AccountDatabase
import me.bookk.database.account.di.accountDaoModule
import me.bookk.database.client.ClientDatabase
import me.bookk.database.client.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

fun clientDatabaseModule(): Module {
    return module {
        includes(accountDaoModule())
        single { ClientDatabase.create(getDatabaseBuilder()) } bind AccountDatabase::class
    }
}
