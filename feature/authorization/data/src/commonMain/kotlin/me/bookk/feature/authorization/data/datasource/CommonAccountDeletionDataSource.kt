package me.bookk.feature.authorization.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.getFlow
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.authorization.data.remote.api.AuthRouting.Api.Auth
import me.bookk.feature.authorization.data.remote.model.AccountDeletionStatusRemote
import me.bookk.feature.authorization.domain.datasource.deletion.AccountDeletionDataSource
import kotlin.time.Instant

class CommonAccountDeletionDataSource(
    private val httpClient: HttpClient,
    preferenceProvider: PreferenceProvider,
) : DataSource(),
    AccountDeletionDataSource,
    LogOutAction {

    private val preferences = preferenceProvider.get("account_deletion_prefs")

    override suspend fun getScheduledDeletion(): Instant? = mapExceptions {
        httpClient.get(Auth.Account.Deletion()).body<AccountDeletionStatusRemote>().deletesAt
    }

    override suspend fun cancelScheduledDeletion(): Unit = mapExceptions {
        httpClient.delete(Auth.Account.Deletion())
    }

    override fun observeScheduledDeletion(): Flow<Instant?> {
        return preferences.getFlow(Key.deletesAt).map { it?.let(Instant::fromEpochMilliseconds) }
    }

    override suspend fun saveScheduledDeletion(deletesAt: Instant?) {
        preferences.set(Key.deletesAt, deletesAt?.toEpochMilliseconds())
    }

    override suspend fun doOnLogOut() {
        preferences.clear()
    }

    private object Key {
        val deletesAt = Preferences.Key<Long>("deletes_at")
    }
}
