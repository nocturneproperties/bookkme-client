package me.bookk.feature.employees.data.di

import me.bookk.feature.employees.data.datasource.EmployeeDataSourceImpl
import me.bookk.feature.employees.data.datasource.EmployeeInvitationDataSourceImpl
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun employeesDataModule() = module {
    singleOf(::EmployeeDataSourceImpl) bind EmployeeDataSource::class
    singleOf(::EmployeeInvitationDataSourceImpl) bind EmployeeInvitationDataSource::class
}
