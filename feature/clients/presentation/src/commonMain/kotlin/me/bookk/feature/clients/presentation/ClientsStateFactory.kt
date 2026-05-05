package me.bookk.feature.clients.presentation

import me.bookk.feature.clients.presentation.list.ClientsListState

interface ClientsStateFactory {
    fun createClientsListState(): ClientsListState
}