package me.bookk.feature.services.presentation.di

import me.bookk.feature.services.presentation.group.add.AddGroupViewModel
import me.bookk.feature.services.presentation.group.list.ServiceGroupListViewModel
import me.bookk.feature.services.presentation.service.add.AddServiceViewModel
import me.bookk.feature.services.presentation.service.list.ServiceListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal actual fun platformServicesDiModule(): Module = module {
    viewModelOf(::ServiceListViewModel)
    viewModelOf(::AddServiceViewModel)
    viewModelOf(::ServiceGroupListViewModel)
    viewModelOf(::AddGroupViewModel)
}
