package me.bookk.feature.clients.presentation

import me.bookk.feature.clients.presentation.create.AndroidCreateClientState
import me.bookk.feature.clients.presentation.create.CreateClientState
import me.bookk.feature.clients.presentation.details.AndroidClientDetailsState
import me.bookk.feature.clients.presentation.details.ClientDetailsState
import me.bookk.feature.clients.presentation.edit.AndroidEditClientState
import me.bookk.feature.clients.presentation.edit.EditClientState
import me.bookk.feature.clients.presentation.list.AndroidClientsListState
import me.bookk.feature.clients.presentation.list.ClientsListState

class AndroidClientsStateFactory : ClientsStateFactory {
    override fun createClientsListState(): ClientsListState {
        return AndroidClientsListState()
    }

    override fun createClientState(): CreateClientState {
        return AndroidCreateClientState()
    }

    override fun createClientDetailsState(): ClientDetailsState {
        return AndroidClientDetailsState()
    }

    override fun createEditClientState(): EditClientState {
        return AndroidEditClientState()
    }
}