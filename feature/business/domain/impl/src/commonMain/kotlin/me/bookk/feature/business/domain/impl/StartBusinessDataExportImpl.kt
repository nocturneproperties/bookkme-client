package me.bookk.feature.business.domain.impl

import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.business.domain.api.StartBusinessDataExport
import me.bookk.feature.business.domain.datasource.BusinessErrorCodes
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import kotlin.uuid.Uuid

internal class StartBusinessDataExportImpl(
    private val dataSource: DataExportDataSource,
    private val confirmation: ConfirmSensitiveAction,
) : StartBusinessDataExport {
    override suspend fun invoke(businessId: Uuid): DataExport {
        val targetId = businessId
        val token = runCatching {
            confirmation(SensitiveAction.EXPORT_BUSINESS_DATA, businessId, targetId)
        }
            .getOrElse {
                throw when (it) {
                    is ConfirmSensitiveAction.Error ->
                        StartBusinessDataExport.Error.ConfirmationFailed()

                    else -> it
                }
            }
        return runCatching { dataSource.start(businessId, token) }
            .onBusinessError {
                when (it.errorCode) {
                    BusinessErrorCodes.BUSINESS_STEP_UP_INVALID ->
                        throw StartBusinessDataExport.Error.ConfirmationRequired()

                    BusinessErrorCodes.DATA_EXPORT_TOO_FREQUENT ->
                        throw StartBusinessDataExport.Error.TooFrequent()
                }
            }
            .getOrThrow()
    }
}
