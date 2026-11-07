package me.bookk.feature.business.domain.impl

import me.bookk.core.domain.entity.DataExport
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.business.domain.api.StartBusinessDataExport
import me.bookk.feature.business.domain.api.StartBusinessDataExport.Error
import me.bookk.feature.business.domain.datasource.BusinessErrorCodes
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

internal class StartBusinessDataExportImpl(
    private val dataSource: DataExportDataSource,
    private val confirmation: ConfirmSensitiveAction,
) : StartBusinessDataExport {
    override suspend fun invoke(businessId: Uuid): DataExport {
        val token = getConfirmationToken(businessId)
        return requestDataExport(businessId, token).also {
            dataSource.deleteFiles(businessId)
            dataSource.saveInDb(businessId, it)
        }
    }

    private suspend fun requestDataExport(businessId: Uuid, token: String): DataExport {
        return runCatching { dataSource.start(businessId, token) }
            .getOrElse { throw mapStartError(businessId, it) }
    }

    private suspend fun mapStartError(businessId: Uuid, error: Throwable): Throwable {
        return when {
            error is DomainError.NotFound -> {
                dataSource.forgetExport(businessId)
                Error.NotOwner()
            }

            error !is DomainError.BusinessError -> {
                error
            }

            error.errorCode == BusinessErrorCodes.BUSINESS_ACTION_CONFIRMATION_INVALID -> {
                Error.ConfirmationRequired()
            }

            error.errorCode == BusinessErrorCodes.DATA_EXPORT_TOO_FREQUENT -> {
                Error.TooFrequent()
            }

            else -> {
                error
            }
        }
    }

    private suspend fun getConfirmationToken(businessId: Uuid): String {
        return runCatching {
            confirmation(SensitiveAction.EXPORT_BUSINESS_DATA, businessId, businessId).token
        }.getOrElse {
            throw if (it is ConfirmSensitiveAction.Error) Error.ConfirmationFailed() else it
        }
    }
}
