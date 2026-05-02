package me.bookk.feature.business.presentation

import me.bookk.feature.business.presentation.clients.list.ClientsListState

interface ClientsStateFactory {
    fun createClientsListState(): ClientsListState
}