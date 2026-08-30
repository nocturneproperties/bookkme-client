package me.bookk.feature.employees.data.di

import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.employees.data.datasource.EmployeeDataSourceImpl
import me.bookk.feature.employees.data.datasource.EmployeeInvitationDataSourceImpl
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

fun employeesDataModule() = module {
    singleOf(::EmployeeDataSourceImpl) binds arrayOf(EmployeeDataSource::class, LogOutAction::class)
    singleOf(::EmployeeInvitationDataSourceImpl) binds arrayOf(EmployeeInvitationDataSource::class, LogOutAction::class)
}
