package me.bookk.database.di

import me.bookk.database.AccountDaos
import me.bookk.database.AppointmentDaos
import me.bookk.database.BusinessDaos
import org.koin.core.module.Module
import org.koin.dsl.module

fun accountDaoModule(): Module {
    return module {
        factory { get<AccountDaos>().profileDao() }
        factory { get<AccountDaos>().notificationSettingsDao() }
        factory { get<AccountDaos>().legalDocumentStatusDao() }
        factory { get<AccountDaos>().personalDataExportDao() }
    }
}

fun businessDaoModule(): Module {
    return module {
        factory { get<BusinessDaos>().businessDao() }
        factory { get<BusinessDaos>().businessDataExportDao() }
    }
}

fun appointmentDaoModule(): Module {
    return module {
        factory { get<AppointmentDaos>().appointmentDao() }
        factory { get<AppointmentDaos>().appointmentRequestDao() }
        factory { get<AppointmentDaos>().appointmentSettingsDao() }
    }
}
