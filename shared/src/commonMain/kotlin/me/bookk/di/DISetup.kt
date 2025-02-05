package me.bookk.di

import me.bookk.core.LogFactory
import me.bookk.presentation.StateFactoryCreator
import me.bookk.shared.LoggerImpl
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initDI(creator: StateFactoryCreator, setup: KoinApplication.() -> Unit) {
    LogFactory.initFactory { LoggerImpl(it) }
    startKoin {
        setup()
        installModules(creator)
    }
}


@Suppress("unused")
fun initDI(creator: StateFactoryCreator) {
    initDI(creator) { }
}

private fun KoinApplication.installModules(creator: StateFactoryCreator) = modules(
    coreModule(creator),
    platformDiModule(),
    authDiModule(),
    dashboardDiModule()
)
