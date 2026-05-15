package me.bookk.feature.services.presentation

import me.bookk.feature.services.presentation.service.add.AddServiceState
import me.bookk.feature.services.presentation.service.add.AndroidAddServiceState
import me.bookk.feature.services.presentation.service.list.AndroidServiceListState
import me.bookk.feature.services.presentation.service.list.ServiceListState

class AndroidServicesStateFactory : ServicesStateFactory {
    override fun createServiceListState(): ServiceListState {
        return AndroidServiceListState()
    }

    override fun createServiceState(): AddServiceState {
        return AndroidAddServiceState()
    }
}
