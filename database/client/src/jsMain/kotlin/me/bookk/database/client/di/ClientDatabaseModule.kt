package me.bookk.database.client.di

import me.bookk.database.AccountDaos
import me.bookk.database.AppointmentDaos
import me.bookk.database.ClientDaos
import me.bookk.database.client.ClientDatabase
import me.bookk.database.client.getDatabaseBuilder
import me.bookk.database.di.accountDaoModule
import me.bookk.database.di.appointmentDaoModule
import me.bookk.database.di.clientDaoModule
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module

fun clientDatabaseModule(): Module {
    return module {
        includes(accountDaoModule(), clientDaoModule(), appointmentDaoModule())
        single { ClientDatabase.create(getDatabaseBuilder()) } binds
            arrayOf(AccountDaos::class, ClientDaos::class, AppointmentDaos::class)
    }
}
