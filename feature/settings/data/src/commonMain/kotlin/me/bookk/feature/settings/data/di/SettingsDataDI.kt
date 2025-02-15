package me.bookk.feature.settings.data.di

import me.bookk.feature.settings.data.datasource.CommonSettingsDataSource
import me.bookk.feature.settings.domain.datasource.SettingsDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun settingsDataModule() = module {
    singleOf(::CommonSettingsDataSource) bind SettingsDataSource::class
}