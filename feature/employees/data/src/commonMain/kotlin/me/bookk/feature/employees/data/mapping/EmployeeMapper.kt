package me.bookk.feature.employees.data.mapping

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import library.money.api.Money
import me.bookk.database.entity.EmployeeDayOffEntity
import me.bookk.database.entity.EmployeeDayScheduleEntity
import me.bookk.database.entity.EmployeeEntity
import me.bookk.database.entity.EmployeeServiceSnapshotEntity
import me.bookk.database.entity.EmployeeWorkHourEntity
import me.bookk.database.relation.EmployeeLocal
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.entity.Service

internal fun Employee.toEntity(): EmployeeEntity {
    return EmployeeEntity(
        id = id,
        businessId = businessId,
        name = name,
        lastName = lastName,
        phone = phone,
        email = email,
        userId = userId,
        createdAt = createdAt
    )
}

internal fun Employee.toDayScheduleEntities() = schedule.days.map { (dayOfWeek, daySchedule) ->
    EmployeeDayScheduleEntity(
        employeeId = id,
        dayOfWeek = dayOfWeek.name,
        isActive = daySchedule.isActive
    )
}

internal fun Employee.toWorkHourEntities() = schedule.days.flatMap { (dayOfWeek, daySchedule) ->
    daySchedule.workingTime.map { workHour ->
        EmployeeWorkHourEntity(
            employeeId = id,
            dayOfWeek = dayOfWeek.name,
            from = workHour.from.toString(),
            to = workHour.to.toString()
        )
    }
}

internal fun Employee.toDayOffEntities() = schedule.dayOffs.map { dayOff ->
    EmployeeDayOffEntity(
        employeeId = id,
        start = dayOff.start.toString(),
        end = dayOff.end.toString()
    )
}

internal fun Employee.toServiceSnapshotEntities() = services.map { service ->
    EmployeeServiceSnapshotEntity(
        employeeId = id,
        id = service.id,
        businessId = service.businessId,
        groupId = service.group.id,
        groupBusinessId = service.group.businessId,
        groupName = service.group.name,
        groupCreatedAt = service.group.createdAt,
        name = service.name,
        duration = service.duration,
        priceCurrency = service.price.currencyType.code,
        priceValue = service.price.value,
        isAvailable = service.isAvailable,
        createdAt = service.createdAt
    )
}

internal fun EmployeeLocal.toDomain(): Employee {
    return Employee(
        id = entity.id,
        businessId = entity.businessId,
        name = entity.name,
        lastName = entity.lastName,
        phone = entity.phone,
        email = entity.email,
        userId = entity.userId,
        services = services.map { it.toDomain() },
        schedule = WorkingSchedule(
            days = daySchedules.associate { daySchedule ->
                val dayOfWeek = DayOfWeek.valueOf(daySchedule.dayOfWeek)
                dayOfWeek to DayOfWeekSchedule(
                    dayOfWeek = dayOfWeek,
                    workingTime = workHours.filter { it.dayOfWeek == daySchedule.dayOfWeek }.map {
                        WorkHour(from = LocalTime.parse(it.from), to = LocalTime.parse(it.to))
                    },
                    isActive = daySchedule.isActive
                )
            },
            dayOffs = dayOffs.map {
                DayOffRange(start = LocalDate.parse(it.start), end = LocalDate.parse(it.end))
            }
        ),
        createdAt = entity.createdAt
    )
}

private fun EmployeeServiceSnapshotEntity.toDomain(): Service {
    return Service(
        id = id,
        businessId = businessId,
        group = ServiceGroup(
            id = groupId,
            businessId = groupBusinessId,
            name = groupName,
            createdAt = groupCreatedAt
        ),
        name = name,
        duration = duration,
        price = Money(value = priceValue, currencyType = Money.SupportedCurrency.fromCode(priceCurrency)),
        isAvailable = isAvailable,
        createdAt = createdAt
    )
}
