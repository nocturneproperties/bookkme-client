package library.permissions.impl.di

import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionType
import library.permissions.impl.checker.NotificationPermissionChecker
import me.bookk.core.android.AndroidActivityAware
import org.koin.core.module.Module
import org.koin.core.qualifier.qualifier
import org.koin.dsl.binds
import org.koin.dsl.module

internal actual fun platformPermissionsModule(): Module = module {
    single(qualifier(PermissionType.NOTIFICATIONS)) { NotificationPermissionChecker() } binds arrayOf(AndroidActivityAware::class, PermissionChecker::class)
}