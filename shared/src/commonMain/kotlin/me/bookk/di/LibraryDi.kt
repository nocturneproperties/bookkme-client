package me.bookk.di

import library.cache.impl.di.cacheModule
import library.credentials.impl.di.credentialsModule
import library.device.impl.di.deviceModule
import library.files.impl.di.filesModule
import library.notifications.impl.di.notificationsModule
import library.permissions.impl.di.permissionsModule
import library.picker.di.pickerModule
import library.validation.impl.di.validationModule
import org.koin.dsl.module

internal fun libraryModule() = module {
    includes(
        filesModule(),
        permissionsModule(),
        cacheModule(),
        deviceModule(),
        credentialsModule(),
        validationModule(),
        pickerModule(),
        notificationsModule()
    )
}