package me.bookk.site.di

import library.cache.impl.di.cacheModule
import library.device.impl.di.deviceModule
import library.files.impl.di.filesModule
import me.bookk.database.client.di.clientDatabaseModule
import me.bookk.feature.authorization.data.di.authDataModule
import me.bookk.feature.authorization.domain.impl.di.authDomainModule
import me.bookk.feature.authorization.presentation.di.authPresentationModule
import me.bookk.feature.settings.data.di.settingsDataModule
import me.bookk.feature.settings.domain.impl.di.settingsDomainModule
import me.bookk.shared.common.di.commonModule
import me.bookk.shared.common.di.initLogging
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

fun initClientApp(setup: KoinApplication.() -> Unit = {}) {
    if (KoinPlatform.getKoinOrNull() != null) return
    initLogging()
    startKoin {
        setup()
        modules(
            commonModule(),
            deviceModule(),
            cacheModule(),
            filesModule(),
            clientDatabaseModule(),
            authDomainModule(),
            authDataModule(),
            settingsDomainModule(),
            settingsDataModule(),
            authPresentationModule(),
        )
    }
}
