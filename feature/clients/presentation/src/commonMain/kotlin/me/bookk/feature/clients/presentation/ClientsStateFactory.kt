package me.bookk.feature.clients.presentation

import me.bookk.feature.clients.presentation.create.CreateClientState
import me.bookk.feature.clients.presentation.details.ClientDetailsState
import me.bookk.feature.clients.presentation.list.ClientsListState

interface ClientsStateFactory {
    fun createClientsListState(): ClientsListState
    fun createClientState(): CreateClientState
    fun createClientDetailsState(): ClientDetailsState
}