package me.bookk.feature.platform.domain.impl.di

import me.bookk.feature.platform.domain.api.GetPlatformInformation
import me.bookk.feature.platform.domain.api.OpenUrlPreview
import me.bookk.feature.platform.domain.impl.GetPlatformInformationImpl
import me.bookk.feature.platform.domain.impl.OpenUrlPreviewImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun platformDomainModule() = module {
    factoryOf(::GetPlatformInformationImpl) bind GetPlatformInformation::class
    factoryOf(::OpenUrlPreviewImpl) bind OpenUrlPreview::class
}