package me.bookk.feature.employees.data.mapping

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import library.money.api.Money
import me.bookk.database.entity.BookableEmployeeDayOffEntity
import me.bookk.database.entity.BookableEmployeeDayScheduleEntity
import me.bookk.database.entity.BookableEmployeeEntity
import me.bookk.database.entity.BookableEmployeeServiceEntity
import me.bookk.database.entity.BookableEmployeeWorkHourEntity
import me.bookk.database.relation.BookableEmployeeLocal
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.entity.Service

internal fun BookableEmployee.toEntity(position: Int): BookableEmployeeEntity {
    return BookableEmployeeEntity(
        id = id,
        businessId = businessId,
        name = name,
        lastName = lastName,
        position = position,
    )
}

internal fun BookableEmployee.toServiceEntities(): List<BookableEmployeeServiceEntity> {
    return services.map { service ->
        BookableEmployeeServiceEntity(
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
            createdAt = service.createdAt,
        )
    }
}

internal fun BookableEmployee.toDayScheduleEntities(): List<BookableEmployeeDayScheduleEntity> {
    return schedule.days.map { (dayOfWeek, daySchedule) ->
        BookableEmployeeDayScheduleEntity(
            employeeId = id,
            dayOfWeek = dayOfWeek.name,
            isActive = daySchedule.isActive,
        )
    }
}

internal fun BookableEmployee.toWorkHourEntities(): List<BookableEmployeeWorkHourEntity> {
    return schedule.days.flatMap { (dayOfWeek, daySchedule) ->
        daySchedule.workingTime.map { workHour ->
            BookableEmployeeWorkHourEntity(
                employeeId = id,
                dayOfWeek = dayOfWeek.name,
                from = workHour.from.toString(),
                to = workHour.to.toString(),
            )
        }
    }
}

internal fun BookableEmployee.toDayOffEntities(): List<BookableEmployeeDayOffEntity> {
    return schedule.dayOffs.map { dayOff ->
        BookableEmployeeDayOffEntity(
            employeeId = id,
            start = dayOff.start.toString(),
            end = dayOff.end.toString(),
        )
    }
}

internal fun BookableEmployeeLocal.toDomain(): BookableEmployee {
    return BookableEmployee(
        id = entity.id,
        businessId = entity.businessId,
        name = entity.name,
        lastName = entity.lastName,
        services = services.map { it.toDomain() },
        schedule = toSchedule(),
    )
}

private fun BookableEmployeeLocal.toSchedule(): WorkingSchedule {
    return WorkingSchedule(
        days = daySchedules.associate { daySchedule ->
            val dayOfWeek = DayOfWeek.valueOf(daySchedule.dayOfWeek)
            dayOfWeek to DayOfWeekSchedule(
                dayOfWeek = dayOfWeek,
                workingTime = workHours
                    .filter { it.dayOfWeek == daySchedule.dayOfWeek }
                    .map { WorkHour(from = LocalTime.parse(it.from), to = LocalTime.parse(it.to)) },
                isActive = daySchedule.isActive,
            )
        },
        dayOffs = dayOffs.map {
            DayOffRange(start = LocalDate.parse(it.start), end = LocalDate.parse(it.end))
        },
    )
}

private fun BookableEmployeeServiceEntity.toDomain(): Service {
    return Service(
        id = id,
        businessId = businessId,
        group = ServiceGroup(
            id = groupId,
            businessId = groupBusinessId,
            name = groupName,
            createdAt = groupCreatedAt,
        ),
        name = name,
        duration = duration,
        price = Money(
            value = priceValue,
            currencyType = Money.SupportedCurrency.fromCode(priceCurrency),
        ),
        isAvailable = isAvailable,
        createdAt = createdAt,
    )
}
