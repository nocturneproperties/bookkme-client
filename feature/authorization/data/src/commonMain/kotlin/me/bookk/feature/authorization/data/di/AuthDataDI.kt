package me.bookk.feature.authorization.data.di

import me.bookk.core.data.HttpClientType
import me.bookk.core.data.mock.RoutingMock
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.authorization.data.datasource.CommonAuthorizationDataSource
import me.bookk.feature.authorization.data.datasource.CommonDeviceDataSource
import me.bookk.feature.authorization.data.datasource.CommonRegistrationDataSource
import me.bookk.feature.authorization.data.datasource.CommonUserProfileDataSource
import me.bookk.feature.authorization.data.remote.mock.UserRoutingMock
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.authorization.domain.datasource.profile.UserProfileDataSource
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal expect fun authDataPlatformModule(): Module

fun authDataModule() = module {
    includes(authDataPlatformModule())
    single {
        CommonAuthorizationDataSource(
            get(),
            get(named(HttpClientType.NO_AUTH)),
            get()
        )
    } binds arrayOf(AuthorizationDataSource::class, LogOutAction::class)
    singleOf(::CommonRegistrationDataSource) bind RegistrationDataSource::class
    singleOf(::CommonDeviceDataSource) bind DeviceDataSource::class
    singleOf(::CommonUserProfileDataSource) binds arrayOf(UserProfileDataSource::class, LogOutAction::class)
    singleOf(::UserRoutingMock) bind RoutingMock::class
}