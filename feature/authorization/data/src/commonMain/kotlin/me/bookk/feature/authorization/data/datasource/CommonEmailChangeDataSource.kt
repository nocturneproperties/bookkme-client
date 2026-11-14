package me.bookk.feature.authorization.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.feature.authorization.data.mapping.toDomain
import me.bookk.feature.authorization.data.remote.api.AuthRouting.Api.Auth
import me.bookk.feature.authorization.data.remote.model.EmailChangeRequestRemote
import me.bookk.feature.authorization.data.remote.model.EmailConfirmationRemote
import me.bookk.feature.authorization.data.remote.model.EmailVerificationStartedRemote
import me.bookk.feature.authorization.domain.datasource.email.EmailChangeDataSource
import me.bookk.feature.authorization.domain.entity.EmailVerification
import kotlin.uuid.Uuid

internal class CommonEmailChangeDataSource(
    private val client: HttpClient,
) : DataSource(),
    EmailChangeDataSource {

    override suspend fun startEmailChange(email: String): EmailVerification {
        return mapExceptions {
            val response = client.post(Auth.Email()) {
                setBody(EmailChangeRequestRemote(email))
            }
            response.body<EmailVerificationStartedRemote>().toDomain()
        }
    }

    override suspend fun confirmEmailChange(verificationId: Uuid, code: String) {
        mapExceptions {
            client.post(Auth.Email.Confirm()) {
                setBody(EmailConfirmationRemote(verificationId, code))
            }
        }
    }
}
