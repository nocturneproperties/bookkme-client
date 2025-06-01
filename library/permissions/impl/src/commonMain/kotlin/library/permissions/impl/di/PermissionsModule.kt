package library.permissions.impl.di

import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionManager
import library.permissions.impl.checker.AlwaysFalsePermissionChecker
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformPermissionsModule(): Module

fun permissionsModule() = module {
    includes(platformPermissionsModule())
    factory<PermissionManager> {
        PermissionManager(
            checkers = getAll<PermissionChecker>()
                .groupBy { it.type }
                .mapValues { it.value.first() },
            fallbackChecker = AlwaysFalsePermissionChecker()
        )
    }
}