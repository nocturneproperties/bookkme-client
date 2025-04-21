package me.bookk.feature.business.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.Business

interface ObserveBusinessChanges {
    operator fun invoke(): Flow<Business?>
}