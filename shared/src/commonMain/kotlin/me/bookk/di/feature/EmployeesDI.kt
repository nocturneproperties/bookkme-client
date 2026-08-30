package me.bookk.di.feature

import me.bookk.feature.employees.data.di.employeesDataModule
import me.bookk.feature.employees.domain.impl.di.employeesDomainModule
import me.bookk.feature.employees.presentation.di.employeesPresentationModule
import org.koin.dsl.module

internal fun employeesDiModule() = module {
    includes(
        employeesPresentationModule(),
        employeesDomainModule(),
        employeesDataModule()
    )
}
