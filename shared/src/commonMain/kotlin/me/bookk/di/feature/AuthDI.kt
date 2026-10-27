package me.bookk.di.feature

import me.bookk.feature.authorization.data.di.authDataModule
import me.bookk.feature.authorization.domain.impl.di.authDomainModule
import me.bookk.feature.authorization.presentation.di.authPresentationModule
import org.koin.dsl.module

internal fun authDiModule() = module {
    includes(
        authPresentationModule(),
        authDomainModule(),
        authDataModule()
    )
}