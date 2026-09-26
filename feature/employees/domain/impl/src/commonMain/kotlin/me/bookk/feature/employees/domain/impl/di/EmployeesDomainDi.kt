package me.bookk.feature.employees.domain.impl.di

import me.bookk.feature.employees.domain.api.CanEditEmployees
import me.bookk.feature.employees.domain.api.CreateEmployeeInvitation
import me.bookk.feature.employees.domain.api.GetAssignableServices
import me.bookk.feature.employees.domain.api.GetEmployee
import me.bookk.feature.employees.domain.api.GetEmployeeInvitations
import me.bookk.feature.employees.domain.api.GetEmployees
import me.bookk.feature.employees.domain.api.IsBusinessOwner
import me.bookk.feature.employees.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.employees.domain.api.RedeemEmployeeInvitation
import me.bookk.feature.employees.domain.api.RevokeEmployeeInvitation
import me.bookk.feature.employees.domain.api.UpdateEmployee
import me.bookk.feature.employees.domain.impl.CanEditEmployeesImpl
import me.bookk.feature.employees.domain.impl.CreateEmployeeInvitationImpl
import me.bookk.feature.employees.domain.impl.GetAssignableServicesImpl
import me.bookk.feature.employees.domain.impl.GetEmployeeImpl
import me.bookk.feature.employees.domain.impl.GetEmployeeInvitationsImpl
import me.bookk.feature.employees.domain.impl.GetEmployeesImpl
import me.bookk.feature.employees.domain.impl.IsBusinessOwnerImpl
import me.bookk.feature.employees.domain.impl.ObserveCurrentBusinessIdImpl
import me.bookk.feature.employees.domain.impl.RedeemEmployeeInvitationImpl
import me.bookk.feature.employees.domain.impl.RevokeEmployeeInvitationImpl
import me.bookk.feature.employees.domain.impl.UpdateEmployeeImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun employeesDomainModule() = module {
    factoryOf(::UpdateEmployeeImpl) bind UpdateEmployee::class
    factoryOf(::CreateEmployeeInvitationImpl) bind CreateEmployeeInvitation::class
    factoryOf(::GetEmployeeInvitationsImpl) bind GetEmployeeInvitations::class
    factoryOf(::GetEmployeesImpl) bind GetEmployees::class
    factoryOf(::GetEmployeeImpl) bind GetEmployee::class
    factoryOf(::GetAssignableServicesImpl) bind GetAssignableServices::class
    factoryOf(::RevokeEmployeeInvitationImpl) bind RevokeEmployeeInvitation::class
    factoryOf(::RedeemEmployeeInvitationImpl) bind RedeemEmployeeInvitation::class
    factoryOf(::ObserveCurrentBusinessIdImpl) bind ObserveCurrentBusinessId::class
    factoryOf(::IsBusinessOwnerImpl) bind IsBusinessOwner::class
    factoryOf(::CanEditEmployeesImpl) bind CanEditEmployees::class
}
