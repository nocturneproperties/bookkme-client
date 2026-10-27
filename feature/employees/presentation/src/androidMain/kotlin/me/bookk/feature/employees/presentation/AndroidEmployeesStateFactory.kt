package me.bookk.feature.employees.presentation

import me.bookk.feature.employees.presentation.screen.edit.AndroidEditEmployeeState
import me.bookk.feature.employees.presentation.screen.edit.AndroidResourcePermissionState
import me.bookk.feature.employees.presentation.screen.edit.EditEmployeeState
import me.bookk.feature.employees.presentation.screen.edit.ResourcePermissionState
import me.bookk.feature.employees.presentation.screen.invite.AndroidInviteEmployeeState
import me.bookk.feature.employees.presentation.screen.invite.InviteEmployeeState
import me.bookk.feature.employees.presentation.screen.list.AndroidEmployeeListState
import me.bookk.feature.employees.presentation.screen.list.EmployeeListState

class AndroidEmployeesStateFactory : EmployeesStateFactory {
    override fun createEmployeeListState(): EmployeeListState {
        return AndroidEmployeeListState()
    }

    override fun createInviteEmployeeState(): InviteEmployeeState {
        return AndroidInviteEmployeeState()
    }

    override fun createEditEmployeeState(): EditEmployeeState {
        return AndroidEditEmployeeState()
    }

    override fun createResourcePermissionState(): ResourcePermissionState {
        return AndroidResourcePermissionState()
    }
}
