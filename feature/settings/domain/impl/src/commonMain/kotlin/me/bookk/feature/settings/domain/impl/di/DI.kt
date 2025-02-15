package me.bookk.feature.settings.domain.impl.di

import me.bookk.feature.settings.domain.api.EditProfile
import me.bookk.feature.settings.domain.api.GetColorScheme
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.UpdateColorScheme
import me.bookk.feature.settings.domain.impl.EditProfileImpl
import me.bookk.feature.settings.domain.impl.GetColorSchemeImpl
import me.bookk.feature.settings.domain.impl.GetSettingsImpl
import me.bookk.feature.settings.domain.impl.UpdateColorSchemeImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun settingsDomainModule() = module {
    factoryOf(::GetColorSchemeImpl) bind GetColorScheme::class
    factoryOf(::UpdateColorSchemeImpl) bind UpdateColorScheme::class
    factoryOf(::GetSettingsImpl) bind GetSettings::class
    factoryOf(::EditProfileImpl) bind EditProfile::class
}