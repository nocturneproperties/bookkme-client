package me.bookk.feature.employees.presentation

import me.bookk.feature.employees.presentation.screen.edit.EditEmployeeState
import me.bookk.feature.employees.presentation.screen.edit.ResourcePermissionState
import me.bookk.feature.employees.presentation.screen.invite.InviteEmployeeState
import me.bookk.feature.employees.presentation.screen.list.EmployeeListState

interface EmployeesStateFactory {
    fun createEmployeeListState(): EmployeeListState
    fun createInviteEmployeeState(): InviteEmployeeState
    fun createEditEmployeeState(): EditEmployeeState
    fun createResourcePermissionState(): ResourcePermissionState
}
