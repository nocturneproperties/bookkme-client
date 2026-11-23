package me.bookk.feature.employees.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.BookableEmployeeDao
import me.bookk.feature.employees.data.mapping.toDayOffEntities
import me.bookk.feature.employees.data.mapping.toDayScheduleEntities
import me.bookk.feature.employees.data.mapping.toDomain
import me.bookk.feature.employees.data.mapping.toEntity
import me.bookk.feature.employees.data.mapping.toServiceEntities
import me.bookk.feature.employees.data.mapping.toWorkHourEntities
import me.bookk.feature.employees.data.remote.api.EmployeeRouting.Api
import me.bookk.feature.employees.data.remote.model.BookableEmployeeRemote
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import me.bookk.feature.employees.domain.datasource.BookableEmployeeDataSource
import kotlin.uuid.Uuid

internal class CommonBookableEmployeeDataSource(
    private val httpClient: HttpClient,
    private val bookableEmployeeDao: BookableEmployeeDao,
) : DataSource(),
    BookableEmployeeDataSource,
    LogOutAction {

    override suspend fun getBookableEmployees(businessId: Uuid): List<BookableEmployee> =
        mapExceptions {
            httpClient.get(Api.Employee.Bookable(Api.Employee(businessId = businessId)))
                .body<List<BookableEmployeeRemote>>()
                .map { it.toDomain() }
        }

    override fun observeBookableEmployeesInDb(businessId: Uuid): Flow<List<BookableEmployee>> {
        return bookableEmployeeDao.observeEmployees(businessId)
            .map { employees -> employees.map { it.toDomain() } }
            .mapErrors()
    }

    override suspend fun replaceBookableEmployeesInDb(
        businessId: Uuid,
        employees: List<BookableEmployee>,
    ) {
        mapExceptions {
            bookableEmployeeDao.replaceForBusiness(
                businessId = businessId,
                employees = employees.mapIndexed { position, employee ->
                    employee.toEntity(position)
                },
                services = employees.flatMap(BookableEmployee::toServiceEntities),
                daySchedules = employees.flatMap(BookableEmployee::toDayScheduleEntities),
                workHours = employees.flatMap(BookableEmployee::toWorkHourEntities),
                dayOffs = employees.flatMap(BookableEmployee::toDayOffEntities),
            )
        }
    }

    override suspend fun doOnLogOut() {
        bookableEmployeeDao.clear()
    }
}
