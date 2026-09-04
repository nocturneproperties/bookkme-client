package me.bookk.feature.employees.domain.impl.di

import me.bookk.feature.employees.domain.api.CreateEmployeeInvitation
import me.bookk.feature.employees.domain.api.GetEmployeeInvitations
import me.bookk.feature.employees.domain.api.GetEmployeePermissions
import me.bookk.feature.employees.domain.api.GetEmployees
import me.bookk.feature.employees.domain.api.PromoteEmployee
import me.bookk.feature.employees.domain.api.RedeemEmployeeInvitation
import me.bookk.feature.employees.domain.api.RevokeEmployeeInvitation
import me.bookk.feature.employees.domain.api.SetEmployeePermission
import me.bookk.feature.employees.domain.api.UpdateEmployee
import me.bookk.feature.employees.domain.impl.CreateEmployeeInvitationImpl
import me.bookk.feature.employees.domain.impl.GetEmployeeInvitationsImpl
import me.bookk.feature.employees.domain.impl.GetEmployeePermissionsImpl
import me.bookk.feature.employees.domain.impl.GetEmployeesImpl
import me.bookk.feature.employees.domain.impl.PromoteEmployeeImpl
import me.bookk.feature.employees.domain.impl.RedeemEmployeeInvitationImpl
import me.bookk.feature.employees.domain.impl.RevokeEmployeeInvitationImpl
import me.bookk.feature.employees.domain.impl.SetEmployeePermissionImpl
import me.bookk.feature.employees.domain.impl.UpdateEmployeeImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun employeesDomainModule() = module {
    factoryOf(::UpdateEmployeeImpl) bind UpdateEmployee::class
    factoryOf(::PromoteEmployeeImpl) bind PromoteEmployee::class
    factoryOf(::CreateEmployeeInvitationImpl) bind CreateEmployeeInvitation::class
    factoryOf(::GetEmployeeInvitationsImpl) bind GetEmployeeInvitations::class
    factoryOf(::GetEmployeesImpl) bind GetEmployees::class
    factoryOf(::RevokeEmployeeInvitationImpl) bind RevokeEmployeeInvitation::class
    factoryOf(::RedeemEmployeeInvitationImpl) bind RedeemEmployeeInvitation::class
    factoryOf(::GetEmployeePermissionsImpl) bind GetEmployeePermissions::class
    factoryOf(::SetEmployeePermissionImpl) bind SetEmployeePermission::class
}
