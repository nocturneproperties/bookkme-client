package me.bookk.client.di.feature

import me.bookk.feature.employees.data.di.clientEmployeesDataModule
import me.bookk.feature.employees.domain.impl.di.clientEmployeesDomainModule
import org.koin.dsl.module

internal fun employeesDiModule() = module {
    includes(
        clientEmployeesDomainModule(),
        clientEmployeesDataModule(),
    )
}
