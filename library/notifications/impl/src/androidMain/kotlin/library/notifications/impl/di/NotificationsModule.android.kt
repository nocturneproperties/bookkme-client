package library.notifications.impl.di

import library.notifications.api.LocalNotificationManager
import library.notifications.impl.AndroidLocalNotificationManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformNotificationsModule(): Module = module {
    factoryOf(::AndroidLocalNotificationManager) bind LocalNotificationManager::class
}
