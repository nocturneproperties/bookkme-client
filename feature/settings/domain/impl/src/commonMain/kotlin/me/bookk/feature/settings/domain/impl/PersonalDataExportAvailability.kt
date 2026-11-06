package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import kotlin.time.Instant

internal fun DataExport.isExpiredAt(now: Instant): Boolean {
    return expiresAt?.let { it <= now } == true
}

internal fun DataExport.isDownloadableAt(now: Instant): Boolean {
    return status == DataExportStatus.READY && !isExpiredAt(now)
}
