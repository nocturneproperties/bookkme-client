package me.bookk.feature.employees.data.remote.api

import io.ktor.resources.Resource
import kotlin.uuid.Uuid

object EmployeeRouting {
    @Resource("api")
    class Api {
        @Resource("/business/{businessId}/employee")
        class Employee(val parent: Api = Api(), val businessId: Uuid) {
            @Resource("/{id}")
            class Id(val parent: Employee, val id: Uuid) {
                @Resource("/promote")
                class Promote(val parent: Id)
            }
        }

        @Resource("/business/{businessId}/employee_invitation")
        class EmployeeInvitation(val parent: Api = Api(), val businessId: Uuid) {
            @Resource("/{id}/approve")
            class Approve(val parent: EmployeeInvitation, val id: Uuid)
        }
    }
}
