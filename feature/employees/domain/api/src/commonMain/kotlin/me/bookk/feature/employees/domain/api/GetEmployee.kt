package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.Employee
import kotlin.uuid.Uuid

interface GetEmployee {
    suspend operator fun invoke(id: Uuid): Employee

    sealed interface Error {
        class NotFound : Error, Throwable()
    }
}
