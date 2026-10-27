package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.CurrentBusiness

interface ObserveCurrentBusiness {
    operator fun invoke(): Flow<CurrentBusiness?>
}
