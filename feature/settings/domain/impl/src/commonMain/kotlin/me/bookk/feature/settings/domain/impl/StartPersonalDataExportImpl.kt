package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.DataExport
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.settings.domain.api.StartPersonalDataExport
import me.bookk.feature.settings.domain.api.StartPersonalDataExport.Error
import me.bookk.feature.settings.domain.api.entity.ActionOutcome
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import kotlin.uuid.Uuid

internal class StartPersonalDataExportImpl(
    dataSource: DataExportDataSource,
    private val confirmation: ConfirmSensitiveAction,
    private val profile: UserProfileCRUD,
) : StartPersonalDataExport {
    private val exportRequest = PersonalDataExportRequest(dataSource)

    override suspend fun invoke(): ActionOutcome<DataExport> {
        val targetId = profile.get().id
        val token = getConfirmationToken(targetId)
        return ActionOutcome.Completed(exportRequest.start(token))
    }

    private suspend fun getConfirmationToken(targetId: Uuid): String = runCatching {
        confirmation(
            action = SensitiveAction.EXPORT_PERSONAL_DATA,
            businessId = Uuid.NIL,
            targetId = targetId,
        ).token
    }
        .getOrElse {
            throw when (it) {
                is ConfirmSensitiveAction.Error -> {
                    Error.ConfirmationFailed()
                }

                else -> {
                    it
                }
            }
        }
}
