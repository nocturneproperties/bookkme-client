package me.bookk.feature.business.domain.api.business

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.Business

interface ObserveUserBusinessesChanges {
    operator fun invoke(): Flow<List<Business>>
}
