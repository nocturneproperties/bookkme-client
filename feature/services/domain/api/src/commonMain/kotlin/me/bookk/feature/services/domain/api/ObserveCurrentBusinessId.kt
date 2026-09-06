package me.bookk.feature.services.domain.api

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface ObserveCurrentBusinessId {
    operator fun invoke(): Flow<Uuid?>
}
