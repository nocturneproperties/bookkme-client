package me.bookk.feature.authorization.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.feature.authorization.data.local.PassKeyCreator
import me.bookk.feature.authorization.data.mapping.toDomain
import me.bookk.feature.authorization.data.mapping.toRemote
import me.bookk.feature.authorization.data.remote.api.AuthRouting.Api.Auth
import me.bookk.feature.authorization.data.remote.error.AuthErrorCodes
import me.bookk.feature.authorization.data.remote.model.RegistrationChallengeResponse
import me.bookk.feature.authorization.data.remote.model.TokenInfoResponse
import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.api.CreateAccount.UserData
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationData
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationDataSource
import me.bookk.feature.authorization.domain.datasource.registration.ServerChallenge
import me.bookk.feature.authorization.domain.entity.TokenInfo

internal class CommonRegistrationDataSource(
    private val client: HttpClient,
    private val passKeyCreator: PassKeyCreator
) : DataSource(), RegistrationDataSource {
    override suspend fun getSignUpPasskeyChallenge(userData: UserData): ServerChallenge {
        return mapExceptions(
            action = {
                val response = client.post(Auth.SignUp.PassKey.Challenge()) {
                    setBody(userData.toRemote())
                }
                response.body<RegistrationChallengeResponse>().toDomain()
            },
            exceptionMapper = {
                throw when (it.errorCode) {
                    AuthErrorCodes.EMAIL_EXIST -> CreateAccount.Error.EmailAlreadyExist
                    AuthErrorCodes.INVALID_EMAIL_FORMAT -> CreateAccount.Error.InvalidEmailFormat
                    else -> it
                }
            }
        )
    }

    override suspend fun finishRegistration(data: RegistrationData): TokenInfo {
        return mapExceptions(
            action = {
                val response = client.post(Auth.SignUp.PassKey.Validate()) {
                    setBody(data.toRemote())
                }
                response.body<TokenInfoResponse>().toDomain()
            },
            exceptionMapper = {
                throw when (it.errorCode) {
                    AuthErrorCodes.USER_ALREADY_EXIST -> CreateAccount.Error.EmailAlreadyExist
                    AuthErrorCodes.INVALID_EMAIL_FORMAT -> CreateAccount.Error.InvalidEmailFormat
                    AuthErrorCodes.VERIFICATION_FAILED -> CreateAccount.Error.PasskeyVerificationFailed
                    AuthErrorCodes.ACCOUNT_CREATION_FAILED -> CreateAccount.Error.AccountCreationFailed
                    else -> it
                }
            }
        )
    }

    override suspend fun createPasskey(challenge: ServerChallenge) = mapExceptions {
        passKeyCreator.create(challenge)
    }
}