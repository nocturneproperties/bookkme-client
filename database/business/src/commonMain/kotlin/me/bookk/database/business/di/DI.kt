package me.bookk.database.business.di

import me.bookk.database.account.AccountDatabase
import me.bookk.database.account.di.accountDaoModule
import me.bookk.database.business.BusinessDatabase
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect fun databaseBuilderModule(): Module

fun businessDatabaseModule(): Module {
    return module {
        includes(databaseBuilderModule(), accountDaoModule(), businessDaoModule())
        single { BusinessDatabase.create(get()) } bind AccountDatabase::class
    }
}

private fun businessDaoModule(): Module {
    return module {
        factory { get<BusinessDatabase>().businessDao() }
        factory { get<BusinessDatabase>().clientDao() }
        factory { get<BusinessDatabase>().serviceDao() }
        factory { get<BusinessDatabase>().serviceGroupDao() }
        factory { get<BusinessDatabase>().appointmentDao() }
        factory { get<BusinessDatabase>().appointmentRequestDao() }
        factory { get<BusinessDatabase>().appointmentSettingsDao() }
        factory { get<BusinessDatabase>().businessDataExportDao() }
        factory { get<BusinessDatabase>().employeeInvitationDao() }
        factory { get<BusinessDatabase>().employeeDao() }
    }
}
