package me.bookk.feature.clients.presentation

import me.bookk.feature.clients.presentation.create.AndroidCreateClientState
import me.bookk.feature.clients.presentation.create.CreateClientState
import me.bookk.feature.clients.presentation.list.AndroidClientsListState
import me.bookk.feature.clients.presentation.list.ClientsListState

class AndroidClientsStateFactory : ClientsStateFactory {
    override fun createClientsListState(): ClientsListState {
        return AndroidClientsListState()
    }

    override fun createClientState(): CreateClientState {
        return AndroidCreateClientState()
    }
}