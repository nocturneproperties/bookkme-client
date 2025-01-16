package me.bookk.di.authorization

import me.bookk.feature.authorization.data.di.authDataModule
import me.bookk.feature.authorization.domain.impl.di.authDomainModule
import me.bookk.feature.authorization.presentation.di.authPresentationModule
import org.koin.dsl.module

fun authDiModule() = module {
    includes(
        authPresentationModule(),
        authDomainModule(),
        authDataModule()
    )
}