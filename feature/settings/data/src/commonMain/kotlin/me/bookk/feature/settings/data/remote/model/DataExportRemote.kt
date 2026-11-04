package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import kotlin.time.Instant

@Serializable
internal enum class DataExportStatusRemote {
    NONE,
    RUNNING,
    READY,
    FAILED,
    ;

    fun toDomain(): DataExportStatus {
        return when (this) {
            NONE -> DataExportStatus.NONE
            RUNNING -> DataExportStatus.RUNNING
            READY -> DataExportStatus.READY
            FAILED -> DataExportStatus.FAILED
        }
    }
}

@Serializable
internal class DataExportRemote(
    @ProtoNumber(1) val status: DataExportStatusRemote,
    @ProtoNumber(2) val requestedAt: Instant? = null,
    @ProtoNumber(3) val expiresAt: Instant? = null,
    @ProtoNumber(4) val availableAgainAt: Instant? = null,
) {
    fun toDomain(): DataExport {
        return DataExport(status.toDomain(), requestedAt, expiresAt, availableAgainAt)
    }
}

@Serializable
internal class DataExportRequestRemote(@ProtoNumber(1) val stepUpToken: String? = null)
