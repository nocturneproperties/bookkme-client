package me.bookk.database.client.di

import me.bookk.database.client.ClientDatabase
import me.bookk.database.client.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

fun clientDatabaseModule(): Module {
    return module {
        includes(clientDaoModule())
        single { ClientDatabase.create(getDatabaseBuilder()) }
    }
}

private fun clientDaoModule(): Module {
    return module {
        factory { get<ClientDatabase>().profileDao() }
        factory { get<ClientDatabase>().notificationSettingsDao() }
        factory { get<ClientDatabase>().legalDocumentStatusDao() }
        factory { get<ClientDatabase>().personalDataExportDao() }
        factory { get<ClientDatabase>().businessDao() }
        factory { get<ClientDatabase>().businessDataExportDao() }
        factory { get<ClientDatabase>().appointmentDao() }
        factory { get<ClientDatabase>().appointmentRequestDao() }
        factory { get<ClientDatabase>().appointmentSettingsDao() }
    }
}
