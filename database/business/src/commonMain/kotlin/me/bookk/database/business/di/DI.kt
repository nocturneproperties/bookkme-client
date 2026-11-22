package me.bookk.database.business.di

import me.bookk.database.AccountDaos
import me.bookk.database.AppointmentDaos
import me.bookk.database.BusinessDaos
import me.bookk.database.DatabaseStorage
import me.bookk.database.RoomDatabaseStorage
import me.bookk.database.business.BusinessDatabase
import me.bookk.database.di.accountDaoModule
import me.bookk.database.di.appointmentDaoModule
import me.bookk.database.di.businessDaoModule
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module

internal expect fun databaseBuilderModule(): Module

fun businessDatabaseModule(): Module {
    return module {
        includes(
            databaseBuilderModule(),
            accountDaoModule(),
            businessDaoModule(),
            appointmentDaoModule(),
            businessOnlyDaoModule(),
        )
        single { BusinessDatabase.create(get()) } binds
            arrayOf(AccountDaos::class, BusinessDaos::class, AppointmentDaos::class)
        single<DatabaseStorage> { RoomDatabaseStorage(get<BusinessDatabase>()) }
    }
}

private fun businessOnlyDaoModule(): Module {
    return module {
        factory { get<BusinessDatabase>().clientDao() }
        factory { get<BusinessDatabase>().serviceDao() }
        factory { get<BusinessDatabase>().serviceGroupDao() }
        factory { get<BusinessDatabase>().employeeInvitationDao() }
        factory { get<BusinessDatabase>().employeeDao() }
    }
}
