package library.notifications.impl.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformNotificationsModule(): Module

fun notificationsModule() = module {
    includes(platformNotificationsModule())
}
