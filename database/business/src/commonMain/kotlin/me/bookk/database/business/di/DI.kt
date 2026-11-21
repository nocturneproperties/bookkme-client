package me.bookk.database.business.di

import me.bookk.database.business.BusinessDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun databaseBuilderModule(): Module

fun businessDatabaseModule(): Module {
    return module {
        includes(databaseBuilderModule(), businessDaoModule())
        single { BusinessDatabase.create(get()) }
    }
}

private fun businessDaoModule(): Module {
    return module {
        factory { get<BusinessDatabase>().profileDao() }
        factory { get<BusinessDatabase>().notificationSettingsDao() }
        factory { get<BusinessDatabase>().legalDocumentStatusDao() }
        factory { get<BusinessDatabase>().personalDataExportDao() }
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
