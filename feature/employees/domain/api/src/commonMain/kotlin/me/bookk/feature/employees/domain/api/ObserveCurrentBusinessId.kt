package me.bookk.feature.employees.domain.api

import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface ObserveCurrentBusinessId {
    operator fun invoke(): Flow<Uuid?>
}
