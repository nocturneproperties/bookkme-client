package me.bookk.feature.authorization.domain.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.bookk.core.domain.fetch.AppStartAction
import me.bookk.core.domain.fetch.BackgroundDataFetchAction
import me.bookk.core.domain.fetch.DataFetchAction
import me.bookk.feature.authorization.domain.api.GetSignUpLegalDocuments
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

internal class InitialAppDataFetchImpl(
    private val userProfileCRUD: UserProfileCRUD,
    private val authorizationDataSource: AuthorizationDataSource,
    private val clock: Clock,
    private val getSignUpLegalDocuments: GetSignUpLegalDocuments,
    private val applicationScope: CoroutineScope,
    private val appStartActions: List<AppStartAction>,
    private val dataFetchActions: List<DataFetchAction>,
    private val backgroundDataFetchActions: List<BackgroundDataFetchAction>,
) : InitialAppDataFetch {

    override suspend fun timestampProtectedFetch() {
        appStartActions.forEach { runCatching { it.doOnAppStart() } }
        if (!isFetchDue()) return
        getSignUpLegalDocuments.refresh()
        if (!authorizationDataSource.getIsAuthorizedFlow().first()) return
        fetchUserData(isSignIn = false)
    }

    override suspend fun rawFetch() {
        fetchUserData(isSignIn = true)
    }

    private suspend fun fetchUserData(isSignIn: Boolean) {
        userProfileCRUD.updateFromRemote()
        dataFetchActions.forEach { runCatching { it.doOnDataFetch(isSignIn) } }
        launchBackgroundDataFetch()
        authorizationDataSource.saveLastInitialDataFetchAt()
    }

    private fun launchBackgroundDataFetch() {
        applicationScope.launch {
            backgroundDataFetchActions.forEach { runCatching { it.doOnBackgroundDataFetch() } }
        }
    }

    private suspend fun isFetchDue(): Boolean {
        if (!authorizationDataSource.isDatabasePersistent()) return true
        val lastFetchAt = authorizationDataSource.getLastInitialDataFetchAt() ?: return true
        if (clock.now() - lastFetchAt >= MIN_FETCH_INTERVAL) return true
        return isSignedInWithoutProfile()
    }

    private suspend fun isSignedInWithoutProfile(): Boolean {
        return authorizationDataSource.getIsAuthorizedFlow().first() &&
            userProfileCRUD.observe().first() == null
    }

    private companion object {
        val MIN_FETCH_INTERVAL = 30.minutes
    }
}
