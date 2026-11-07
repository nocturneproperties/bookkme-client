package me.bookk.core.domain.entity

import kotlin.time.Instant

enum class DataExportStatus {
    NONE,
    RUNNING,
    READY,
    FAILED,
}

data class DataExport(
    val status: DataExportStatus,
    val requestedAt: Instant? = null,
    val expiresAt: Instant? = null,
    val availableAgainAt: Instant? = null,
) {
    fun isExpiredAt(now: Instant): Boolean {
        return expiresAt?.let { it <= now } == true
    }

    fun isDownloadableAt(now: Instant): Boolean {
        return status == DataExportStatus.READY && !isExpiredAt(now)
    }
}
