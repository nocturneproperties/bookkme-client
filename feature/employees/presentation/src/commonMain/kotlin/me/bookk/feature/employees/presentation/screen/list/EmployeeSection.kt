package me.bookk.feature.employees.presentation.screen.list

import me.bookk.feature.employees.domain.api.entity.Employee

data class EmployeeSection(
    val id: String,
    val header: String,
    val items: List<Employee>
)
