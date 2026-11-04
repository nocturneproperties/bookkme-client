package me.bookk.feature.clients.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.clients.domain.api.DeleteClient
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.datasource.ClientErrorCodes
import me.bookk.feature.clients.domain.datasource.ClientsDataSource

internal class DeleteClientImpl(
    private val clientsDataSource: ClientsDataSource,
    private val confirmation: ConfirmSensitiveAction,
) : DeleteClient {
    override suspend fun invoke(client: Client) {
        val token = runCatching {
            confirmation(SensitiveAction.DELETE_CLIENT, client.businessId, client.id)
        }
            .getOrElse {
                throw when (it) {
                    is ConfirmSensitiveAction.Error -> DeleteClient.Error.ConfirmationFailed()
                    else -> it
                }
            }
        runCatching { clientsDataSource.deleteClient(client.businessId, client.id, token) }
            .onBusinessError {
                if (it.errorCode ==
                    ClientErrorCodes.STEP_UP_INVALID
                ) {
                    throw DeleteClient.Error.ConfirmationRequired()
                }
            }.getOrThrow()
        clientsDataSource.deleteClientInDb(client.id)
    }
}
