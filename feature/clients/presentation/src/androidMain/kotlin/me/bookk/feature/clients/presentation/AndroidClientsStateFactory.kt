package me.bookk.feature.clients.presentation

import me.bookk.feature.clients.presentation.list.AndroidClientsListState
import me.bookk.feature.clients.presentation.list.ClientsListState

class AndroidClientsStateFactory : ClientsStateFactory {
    override fun createClientsListState(): ClientsListState {
        return AndroidClientsListState()
    }
}