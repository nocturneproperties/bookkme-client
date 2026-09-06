package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

internal class InitialAppDataFetchImpl(
    private val userProfileCRUD: UserProfileCRUD,
    private val refreshBusiness: RefreshBusinessInfo,
    private val lowPriorityDataFetch: LowPriorityDataFetch,
    private val authorizationDataSource: AuthorizationDataSource,
    private val clock: Clock
) : InitialAppDataFetch {

    override suspend fun timestampProtectedFetch() {
        if (!isFetchDue()) return
        userProfileCRUD.updateFromRemote()
        runCatching { refreshBusiness(applyDashboardIdFromRemote = false) }
        lowPriorityDataFetch()
        authorizationDataSource.saveLastInitialDataFetchAt()
    }

    override suspend fun rawFetch() {
        userProfileCRUD.updateFromRemote()
        runCatching { refreshBusiness(applyDashboardIdFromRemote = true) }
        lowPriorityDataFetch()
        authorizationDataSource.saveLastInitialDataFetchAt()
    }

    private suspend fun isFetchDue(): Boolean {
        val lastFetchAt = authorizationDataSource.getLastInitialDataFetchAt() ?: return true
        return clock.now() - lastFetchAt >= MIN_FETCH_INTERVAL
    }

    private companion object {
        val MIN_FETCH_INTERVAL = 30.minutes
    }
}
