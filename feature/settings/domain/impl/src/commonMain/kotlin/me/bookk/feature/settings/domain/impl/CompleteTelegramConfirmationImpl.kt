package me.bookk.feature.settings.domain.impl

import me.bookk.core.coroutine.runSuspendCatching
import me.bookk.core.domain.launch.ConfirmableAction
import me.bookk.feature.authorization.domain.api.ConfirmActionWithTelegram
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.deletion.AccountDeletionDataSource
import me.bookk.feature.settings.domain.api.CompleteTelegramConfirmation
import me.bookk.feature.settings.domain.api.CompleteTelegramConfirmation.Error
import me.bookk.feature.settings.domain.api.DeleteAccount
import me.bookk.feature.settings.domain.api.StartPersonalDataExport
import me.bookk.feature.settings.domain.datasource.DataExportDataSource

internal class CompleteTelegramConfirmationImpl(
    private val confirmWithTelegram: ConfirmActionWithTelegram,
    authorizationDataSource: AuthorizationDataSource,
    accountDeletionDataSource: AccountDeletionDataSource,
    dataExportDataSource: DataExportDataSource,
) : CompleteTelegramConfirmation {
    private val deletionScheduler =
        AccountDeletionScheduler(authorizationDataSource, accountDeletionDataSource)
    private val exportRequest = PersonalDataExportRequest(dataSource = dataExportDataSource)

    override suspend fun invoke(action: ConfirmableAction, initData: String) {
        runSuspendCatching {
            val token = confirmWithTelegram(initData)
            complete(action, token)
        }.getOrElse { throw it.toConfirmationError() }
    }

    private suspend fun complete(action: ConfirmableAction, token: String) {
        when (action) {
            ConfirmableAction.DELETE_ACCOUNT -> {
                deletionScheduler.scheduleDeletion(token)
            }

            ConfirmableAction.EXPORT_PERSONAL_DATA -> {
                exportRequest.start(token)
            }
        }
    }

    private fun Throwable.toConfirmationError(): Throwable {
        return when (this) {
            is ConfirmActionWithTelegram.Error.ConfirmationExpired,
            is DeleteAccount.Error.AccountVerificationFailed,
            is StartPersonalDataExport.Error.ConfirmationRequired,
            -> {
                Error.ConfirmationExpired()
            }

            is ConfirmActionWithTelegram.Error.TelegramAccountMismatch -> {
                Error.TelegramAccountMismatch()
            }

            is ConfirmActionWithTelegram.Error.TelegramDataInvalid -> {
                Error.TelegramDataInvalid()
            }

            is StartPersonalDataExport.Error.TooFrequent -> {
                Error.ExportTooFrequent()
            }

            else -> {
                this
            }
        }
    }
}
