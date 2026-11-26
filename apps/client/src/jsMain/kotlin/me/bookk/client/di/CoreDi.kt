package me.bookk.client.di

import me.bookk.database.client.di.clientDatabaseModule
import me.bookk.shared.common.di.commonModule
import org.koin.dsl.module

internal fun coreModule() = module {
    includes(
        commonModule(),
        clientDatabaseModule(),
    )
}
