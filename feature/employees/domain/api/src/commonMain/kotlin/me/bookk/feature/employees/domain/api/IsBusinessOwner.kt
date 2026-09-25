package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.Employee

interface IsBusinessOwner {
    suspend operator fun invoke(employee: Employee): Boolean
}
