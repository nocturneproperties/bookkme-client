package me.bookk.database.account.di

import me.bookk.database.account.AccountDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

fun accountDaoModule(): Module {
    return module {
        factory { get<AccountDatabase>().profileDao() }
        factory { get<AccountDatabase>().notificationSettingsDao() }
        factory { get<AccountDatabase>().legalDocumentStatusDao() }
        factory { get<AccountDatabase>().personalDataExportDao() }
    }
}
