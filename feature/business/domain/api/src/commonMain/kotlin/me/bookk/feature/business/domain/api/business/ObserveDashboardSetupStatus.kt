package me.bookk.feature.business.domain.api.business

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.DashboardSetupStatus

interface ObserveDashboardSetupStatus {
    operator fun invoke(): Flow<DashboardSetupStatus>
}
