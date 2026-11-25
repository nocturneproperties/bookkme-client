package me.bookk.client.di.feature

import me.bookk.feature.business.data.di.clientBusinessDataModule
import me.bookk.feature.business.domain.impl.di.clientBusinessDomainModule
import org.koin.dsl.module

internal fun businessDiModule() = module {
    includes(
        clientBusinessDomainModule(),
        clientBusinessDataModule(),
    )
}
