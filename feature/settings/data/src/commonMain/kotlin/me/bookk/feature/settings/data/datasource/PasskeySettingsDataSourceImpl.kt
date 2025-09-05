package me.bookk.feature.settings.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.feature.settings.data.mapping.toDomain
import me.bookk.feature.settings.data.mapping.toRemote
import me.bookk.feature.settings.data.remote.api.AuthRouting
import me.bookk.feature.settings.data.remote.model.PasskeyRemote
import me.bookk.feature.settings.data.remote.model.RegistrationChallengeResponse
import me.bookk.feature.settings.domain.api.entity.Passkey
import me.bookk.feature.settings.domain.datasource.passkey.ClientSignUpResult
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource
import me.bookk.feature.settings.domain.datasource.passkey.ServerSignUpChallenge
import kotlin.uuid.Uuid

internal class PasskeySettingsDataSourceImpl(
    private val httpClient: HttpClient
) : DataSource(), PasskeySettingsDataSource {
    override suspend fun getPasskeys(): List<Passkey> {
        return mapExceptions {
            httpClient.get(AuthRouting.Api.Auth.PassKey())
                .body<List<PasskeyRemote>>()
                .map(PasskeyRemote::toDomain)
        }
    }

    override suspend fun deletePasskey(id: Uuid) {
        mapExceptions {
            httpClient.delete(AuthRouting.Api.Auth.PassKey.Id(id = id))
        }
    }

    override suspend fun getRegistrationChallengeForNewPasskey(): ServerSignUpChallenge {
        return mapExceptions {
            httpClient.get(AuthRouting.Api.Auth.PassKey.AddChallenge())
                .body<RegistrationChallengeResponse>()
                .toDomain()
        }
    }

    override suspend fun sendVerifiedPasskey(data: ClientSignUpResult) {
        mapExceptions {
            httpClient.post(AuthRouting.Api.Auth.PassKey.AddFinish()) {
                setBody(data.toRemote())
            }
        }
    }
}