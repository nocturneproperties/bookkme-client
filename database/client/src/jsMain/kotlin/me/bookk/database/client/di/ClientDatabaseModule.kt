package me.bookk.database.client.di

import kotlinx.coroutines.CoroutineScope
import me.bookk.database.ClientDaos
import me.bookk.database.DatabaseStorage
import me.bookk.database.ExternalChangeInvalidator
import me.bookk.database.RoomDatabaseStorage
import me.bookk.database.client.ClientDatabase
import me.bookk.database.client.CrossTabInvalidation
import me.bookk.database.client.getDatabaseBuilder
import me.bookk.database.di.clientDaoModule
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

fun clientDatabaseModule(): Module {
    return module {
        includes(clientDaoModule())
        single { createClientDatabase(get()) } bind ClientDaos::class
        single<DatabaseStorage> { RoomDatabaseStorage(get<ClientDatabase>()) }
    }
}

private fun createClientDatabase(scope: CoroutineScope): ClientDatabase {
    val database = ClientDatabase.create(getDatabaseBuilder())
    CrossTabInvalidation(ExternalChangeInvalidator(database), scope).start()
    return database
}
