package me.bookk.feature.settings.domain.api

import me.bookk.core.domain.entity.DataExport
import kotlin.uuid.Uuid

interface GetPersonalDataExport {
    suspend operator fun invoke(): DataExport
}
