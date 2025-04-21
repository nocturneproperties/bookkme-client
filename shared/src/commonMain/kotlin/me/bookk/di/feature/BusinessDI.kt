package me.bookk.di.feature

import me.bookk.feature.business.data.di.businessDataModule
import me.bookk.feature.business.domain.impl.di.businessDomainModule
import me.bookk.feature.business.presentation.di.businessPresentationModule
import org.koin.dsl.module

internal fun businessDiModule() = module {
    includes(
        businessPresentationModule(),
        businessDomainModule(),
        businessDataModule()
    )
}