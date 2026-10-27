package me.bookk.di.feature

import me.bookk.feature.settings.data.di.settingsDataModule
import me.bookk.feature.settings.domain.impl.di.settingsDomainModule
import me.bookk.feature.settings.presentation.di.settingsPresentationModule
import org.koin.dsl.module

internal fun settingsDiModule() = module {
    includes(
        settingsPresentationModule(),
        settingsDataModule(),
        settingsDomainModule()
    )
}