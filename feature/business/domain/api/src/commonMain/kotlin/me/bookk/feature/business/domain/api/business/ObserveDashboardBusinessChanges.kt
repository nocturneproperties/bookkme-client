package me.bookk.feature.business.domain.api.business

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.Business

interface ObserveDashboardBusinessChanges {
    operator fun invoke(): Flow<Business?>
}