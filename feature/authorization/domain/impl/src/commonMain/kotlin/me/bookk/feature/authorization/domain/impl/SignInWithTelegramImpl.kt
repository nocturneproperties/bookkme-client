package me.bookk.feature.authorization.domain.impl

import library.device.api.DeviceFacade
import me.bookk.core.domain.entity.Error
import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.api.SignInWithTelegram
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.SignInData
import me.bookk.feature.authorization.domain.datasource.authorization.TelegramSignInData
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import me.bookk.feature.authorization.domain.entity.TokenInfo

internal class SignInWithTelegramImpl(
    private val deviceDataSource: DeviceDataSource,
    private val authorizationDataSource: AuthorizationDataSource,
    private val deviceFacade: DeviceFacade,
    private val initialAppDataFetch: InitialAppDataFetch,
) : SignInWithTelegram {

    override suspend fun invoke(initData: String, acceptedDocuments: AcceptedLegalDocuments?) {
        val tokenInfo = signIn(createSignInData(initData, acceptedDocuments))
        authorizationDataSource.saveAuthorizationTokens(tokenInfo)
        authorizationDataSource.invalidateClientTokens()
        initialAppDataFetch.rawFetch()
        authorizationDataSource.setAuthorizationStatus(true)
    }

    private suspend fun signIn(data: TelegramSignInData): TokenInfo {
        return runCatching { authorizationDataSource.signInWithTelegram(data) }
            .onBusinessError { throw it.toSignInError() }
            .getOrThrow()
    }

    private fun Error.BusinessError.toSignInError(): Throwable {
        return when (errorCode) {
            AuthErrorCodes.TELEGRAM_DATA_INVALID -> {
                SignInWithTelegram.Error.TelegramDataInvalid()
            }

            AuthErrorCodes.TELEGRAM_DATA_EXPIRED -> {
                SignInWithTelegram.Error.TelegramDataExpired()
            }

            AuthErrorCodes.TELEGRAM_SIGN_IN_UNAVAILABLE -> {
                SignInWithTelegram.Error.Unavailable()
            }

            AuthErrorCodes.ACCOUNT_CREATION_FAILED -> {
                SignInWithTelegram.Error.AccountCreationFailed()
            }

            AuthErrorCodes.LEGAL_DOCUMENT_VERSION_UNKNOWN -> {
                SignInWithTelegram.Error.LegalDocumentVersionUnknown()
            }

            AuthErrorCodes.LEGAL_DOCUMENTS_NOT_ACCEPTED -> {
                SignInWithTelegram.Error.LegalDocumentsNotAccepted()
            }

            else -> {
                this
            }
        }
    }

    private suspend fun createSignInData(
        initData: String,
        acceptedDocuments: AcceptedLegalDocuments?,
    ): TelegramSignInData {
        return TelegramSignInData(
            initData = initData,
            deviceInfo = SignInData.DeviceInfo(
                deviceUUID = deviceDataSource.getOrCreateDeviceUUID(),
                deviceName = deviceFacade.getDeviceName(),
            ),
            acceptedDocuments = acceptedDocuments,
        )
    }
}
