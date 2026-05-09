package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo

internal class InitialAppDataFetchImpl(
    private val userProfileCRUD: UserProfileCRUD,
    private val refreshBusiness: RefreshBusinessInfo
) : InitialAppDataFetch {

    override suspend fun invoke() {
        userProfileCRUD.updateFromRemote()
        runCatching { refreshBusiness() }
    }
}