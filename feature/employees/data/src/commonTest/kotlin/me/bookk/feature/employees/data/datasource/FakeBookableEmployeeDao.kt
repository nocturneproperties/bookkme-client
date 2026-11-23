package me.bookk.feature.employees.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.bookk.database.dao.BookableEmployeeDao
import me.bookk.database.entity.BookableEmployeeDayOffEntity
import me.bookk.database.entity.BookableEmployeeDayScheduleEntity
import me.bookk.database.entity.BookableEmployeeEntity
import me.bookk.database.entity.BookableEmployeeServiceEntity
import me.bookk.database.entity.BookableEmployeeWorkHourEntity
import me.bookk.database.relation.BookableEmployeeLocal
import kotlin.uuid.Uuid

internal class FakeBookableEmployeeDao : BookableEmployeeDao() {

    private data class Tables(
        val employees: List<BookableEmployeeEntity> = emptyList(),
        val services: List<BookableEmployeeServiceEntity> = emptyList(),
        val daySchedules: List<BookableEmployeeDayScheduleEntity> = emptyList(),
        val workHours: List<BookableEmployeeWorkHourEntity> = emptyList(),
        val dayOffs: List<BookableEmployeeDayOffEntity> = emptyList(),
    ) {
        fun withoutEmployees(ids: Set<Uuid>): Tables {
            return Tables(
                employees = employees.filterNot { it.id in ids },
                services = services.filterNot { it.employeeId in ids },
                daySchedules = daySchedules.filterNot { it.employeeId in ids },
                workHours = workHours.filterNot { it.employeeId in ids },
                dayOffs = dayOffs.filterNot { it.employeeId in ids },
            )
        }

        fun localOf(employee: BookableEmployeeEntity): BookableEmployeeLocal {
            return BookableEmployeeLocal(
                entity = employee,
                services = services.filter { it.employeeId == employee.id },
                daySchedules = daySchedules.filter { it.employeeId == employee.id },
                workHours = workHours.filter { it.employeeId == employee.id },
                dayOffs = dayOffs.filter { it.employeeId == employee.id },
            )
        }
    }

    private val tables = MutableStateFlow(Tables())

    override fun observeEmployees(businessId: Uuid): Flow<List<BookableEmployeeLocal>> {
        return tables.map { tables ->
            tables.employees
                .filter { it.businessId == businessId }
                .sortedBy { it.position }
                .map { tables.localOf(it) }
        }
    }

    override suspend fun deleteForBusiness(businessId: Uuid) {
        tables.update { tables ->
            val ids = tables.employees.filter { it.businessId == businessId }.map { it.id }
            tables.withoutEmployees(ids.toSet())
        }
    }

    override suspend fun insertEmployees(employees: List<BookableEmployeeEntity>) {
        tables.update { it.copy(employees = it.employees + employees) }
    }

    override suspend fun insertServices(services: List<BookableEmployeeServiceEntity>) {
        tables.update { it.copy(services = it.services + services) }
    }

    override suspend fun insertDaySchedules(daySchedules: List<BookableEmployeeDayScheduleEntity>) {
        tables.update { it.copy(daySchedules = it.daySchedules + daySchedules) }
    }

    override suspend fun insertWorkHours(workHours: List<BookableEmployeeWorkHourEntity>) {
        tables.update { it.copy(workHours = it.workHours + workHours) }
    }

    override suspend fun insertDayOffs(dayOffs: List<BookableEmployeeDayOffEntity>) {
        tables.update { it.copy(dayOffs = it.dayOffs + dayOffs) }
    }

    override suspend fun clear() {
        tables.value = Tables()
    }
}
