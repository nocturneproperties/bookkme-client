package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.settings.domain.api.StartPersonalDataExport
import me.bookk.feature.settings.domain.api.StartPersonalDataExport.Error
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import me.bookk.feature.settings.domain.datasource.SettingsErrorCodes
import kotlin.uuid.Uuid

internal class StartPersonalDataExportImpl(
    private val dataSource: DataExportDataSource,
    private val confirmation: ConfirmSensitiveAction,
    private val profile: UserProfileCRUD,
) : StartPersonalDataExport {
    override suspend fun invoke(): DataExport {
        val targetId = profile.get().id
        val token = getConfirmationToken(targetId)
        return requestPersonalDataExport(token).also {
            dataSource.deleteFiles()
            dataSource.saveInDb(it)
        }
    }

    private suspend fun requestPersonalDataExport(token: String): DataExport = runCatching {
        dataSource.start(token)
    }.onBusinessError {
        when (it.errorCode) {
            SettingsErrorCodes.USER_ACTION_CONFIRMATION_INVALID ->
                throw Error.ConfirmationRequired()

            SettingsErrorCodes.DATA_EXPORT_TOO_FREQUENT -> throw Error.TooFrequent()
        }
    }.getOrThrow()

    private suspend fun getConfirmationToken(targetId: Uuid): String = runCatching {
        confirmation(
            action = SensitiveAction.EXPORT_PERSONAL_DATA,
            businessId = Uuid.NIL,
            targetId = targetId,
        ).token
    }
        .getOrElse {
            throw when (it) {
                is ConfirmSensitiveAction.Error ->
                    Error.ConfirmationFailed()

                else -> it
            }
        }
}
