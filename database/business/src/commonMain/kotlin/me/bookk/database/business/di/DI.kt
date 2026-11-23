package me.bookk.database.business.di

import me.bookk.database.BusinessDaos
import me.bookk.database.DatabaseStorage
import me.bookk.database.RoomDatabaseStorage
import me.bookk.database.business.BusinessDatabase
import me.bookk.database.di.businessDaoModule
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect fun databaseBuilderModule(): Module

fun businessDatabaseModule(): Module {
    return module {
        includes(
            databaseBuilderModule(),
            businessDaoModule(),
        )
        single { BusinessDatabase.create(get()) } bind BusinessDaos::class
        single<DatabaseStorage> { RoomDatabaseStorage(get<BusinessDatabase>()) }
    }
}
