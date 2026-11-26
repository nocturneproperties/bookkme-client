package me.bookk.client.di.feature

import me.bookk.feature.settings.data.di.settingsDataModule
import me.bookk.feature.settings.domain.impl.di.clientSettingsDomainModule
import me.bookk.feature.settings.presentation.di.settingsPresentationModule
import org.koin.dsl.module

internal fun settingsDiModule() = module {
    includes(
        settingsPresentationModule(),
        clientSettingsDomainModule(),
        settingsDataModule(),
    )
}
