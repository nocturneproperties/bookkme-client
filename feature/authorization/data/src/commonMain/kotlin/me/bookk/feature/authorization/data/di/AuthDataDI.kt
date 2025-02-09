package me.bookk.feature.authorization.data.di

import me.bookk.feature.authorization.data.datasource.CommonAuthorizationDataSource
import me.bookk.feature.authorization.data.datasource.CommonDeviceDataSource
import me.bookk.feature.authorization.data.datasource.CommonRegistrationDataSource
import me.bookk.feature.authorization.data.datasource.CommonUserProfileDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.authorization.domain.datasource.profile.UserProfileDataSource
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect fun authDataPlatformModule(): Module

fun authDataModule() = module {
    includes(authDataPlatformModule())
    singleOf(::CommonRegistrationDataSource) bind RegistrationDataSource::class
    singleOf(::CommonDeviceDataSource) bind DeviceDataSource::class
    singleOf(::CommonAuthorizationDataSource) bind AuthorizationDataSource::class
    singleOf(::CommonUserProfileDataSource) bind UserProfileDataSource::class
}