package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.settings.domain.api.StartPersonalDataExport.Error
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import me.bookk.feature.settings.domain.datasource.SettingsErrorCodes

internal class PersonalDataExportRequest(
    private val dataSource: DataExportDataSource,
) {
    suspend fun start(confirmationToken: String): DataExport {
        return requestExport(confirmationToken).also {
            dataSource.deleteFiles()
            dataSource.saveInDb(it)
        }
    }

    private suspend fun requestExport(confirmationToken: String): DataExport {
        return runCatching {
            dataSource.start(confirmationToken)
        }.onBusinessError {
            when (it.errorCode) {
                SettingsErrorCodes.USER_ACTION_CONFIRMATION_INVALID -> {
                    throw Error.ConfirmationRequired()
                }

                SettingsErrorCodes.DATA_EXPORT_TOO_FREQUENT -> {
                    throw Error.TooFrequent()
                }
            }
        }.getOrThrow()
    }
}
