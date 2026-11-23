package me.bookk.database.di

import me.bookk.database.BusinessDaos
import me.bookk.database.ClientDaos
import org.koin.core.module.Module
import org.koin.dsl.module

fun businessDaoModule(): Module {
    return module {
        factory { get<BusinessDaos>().profileDao() }
        factory { get<BusinessDaos>().notificationSettingsDao() }
        factory { get<BusinessDaos>().legalDocumentStatusDao() }
        factory { get<BusinessDaos>().personalDataExportDao() }
        factory { get<BusinessDaos>().businessDao() }
        factory { get<BusinessDaos>().businessDataExportDao() }
        factory { get<BusinessDaos>().businessLinkDao() }
        factory { get<BusinessDaos>().serviceDao() }
        factory { get<BusinessDaos>().serviceGroupDao() }
        factory { get<BusinessDaos>().appointmentDao() }
        factory { get<BusinessDaos>().appointmentRequestDao() }
        factory { get<BusinessDaos>().appointmentSettingsDao() }
        factory { get<BusinessDaos>().clientDao() }
        factory { get<BusinessDaos>().employeeDao() }
        factory { get<BusinessDaos>().employeeInvitationDao() }
    }
}

fun clientDaoModule(): Module {
    return module {
        factory { get<ClientDaos>().profileDao() }
        factory { get<ClientDaos>().notificationSettingsDao() }
        factory { get<ClientDaos>().legalDocumentStatusDao() }
        factory { get<ClientDaos>().personalDataExportDao() }
        factory { get<ClientDaos>().businessDao() }
        factory { get<ClientDaos>().businessDataExportDao() }
        factory { get<ClientDaos>().savedBusinessDao() }
        factory { get<ClientDaos>().serviceDao() }
        factory { get<ClientDaos>().serviceGroupDao() }
        factory { get<ClientDaos>().appointmentDao() }
        factory { get<ClientDaos>().appointmentRequestDao() }
        factory { get<ClientDaos>().appointmentSettingsDao() }
    }
}
