package me.bookk.feature.business.domain.api.business

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface ObserveDashboardBusinessIdChanges {
    operator fun invoke(): Flow<Uuid?>
}
