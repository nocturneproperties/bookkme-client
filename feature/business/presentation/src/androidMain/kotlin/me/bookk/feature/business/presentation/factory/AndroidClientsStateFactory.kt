package me.bookk.feature.business.presentation.factory

import me.bookk.feature.business.presentation.ClientsStateFactory
import me.bookk.feature.business.presentation.clients.list.AndroidClientsListState
import me.bookk.feature.business.presentation.clients.list.ClientsListState

class AndroidClientsStateFactory : ClientsStateFactory {
    override fun createClientsListState(): ClientsListState {
        return AndroidClientsListState()
    }
}