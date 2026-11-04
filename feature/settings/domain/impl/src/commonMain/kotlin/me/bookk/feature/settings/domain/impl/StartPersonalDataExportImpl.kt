package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.DataExport
import me.bookk.feature.settings.domain.api.StartPersonalDataExport
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import me.bookk.feature.settings.domain.datasource.SettingsErrorCodes
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.authorization.domain.api.UserProfileCRUD

internal class StartPersonalDataExportImpl(
    private val dataSource: DataExportDataSource,
    private val confirmation: ConfirmSensitiveAction,
    private val profile: UserProfileCRUD
) : StartPersonalDataExport {
    override suspend fun invoke(): DataExport {
        val targetId = profile.get().id
        val token = runCatching { confirmation(SensitiveAction.EXPORT_PERSONAL_DATA, Uuid.parse("00000000-0000-0000-0000-000000000000"), targetId) }
            .getOrElse {
                throw when (it) {
                    is ConfirmSensitiveAction.Error -> StartPersonalDataExport.Error.ConfirmationFailed()
                    else -> it
                }
            }
        return runCatching { dataSource.start(token) }
            .onBusinessError {
                when (it.errorCode) {
                    SettingsErrorCodes.USER_STEP_UP_INVALID -> throw StartPersonalDataExport.Error.ConfirmationRequired()
                    SettingsErrorCodes.DATA_EXPORT_TOO_FREQUENT -> throw StartPersonalDataExport.Error.TooFrequent()
                }
            }.getOrThrow()
    }
}
