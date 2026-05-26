package me.bookk.feature.services.presentation

import me.bookk.feature.services.presentation.service.add.AddServiceState
import me.bookk.feature.services.presentation.service.list.ServiceListState

interface ServicesStateFactory {
    fun createServiceListState(): ServiceListState
    fun createServiceState(): AddServiceState
}
