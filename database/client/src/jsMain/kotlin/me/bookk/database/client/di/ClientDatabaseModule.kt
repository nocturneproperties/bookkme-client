package me.bookk.database.client.di

import me.bookk.database.AccountDaos
import me.bookk.database.AppointmentDaos
import me.bookk.database.BusinessDaos
import me.bookk.database.client.ClientDatabase
import me.bookk.database.client.getDatabaseBuilder
import me.bookk.database.di.accountDaoModule
import me.bookk.database.di.appointmentDaoModule
import me.bookk.database.di.businessDaoModule
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module

fun clientDatabaseModule(): Module {
    return module {
        includes(accountDaoModule(), businessDaoModule(), appointmentDaoModule())
        single { ClientDatabase.create(getDatabaseBuilder()) } binds
            arrayOf(AccountDaos::class, BusinessDaos::class, AppointmentDaos::class)
    }
}
