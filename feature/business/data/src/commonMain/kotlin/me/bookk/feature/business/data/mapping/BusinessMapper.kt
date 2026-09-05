package me.bookk.feature.business.data.mapping

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.database.entity.BusinessDayOffEntity
import me.bookk.database.entity.BusinessDayScheduleEntity
import me.bookk.database.entity.BusinessEntity
import me.bookk.database.entity.BusinessWorkHourEntity
import me.bookk.database.relation.BusinessLocal
import me.bookk.feature.business.data.remote.model.BusinessRemote
import me.bookk.feature.business.data.remote.model.BusinessUpdateRemote
import me.bookk.feature.business.data.remote.model.UserBusinessesRemote
import me.bookk.feature.business.data.remote.model.toRemote
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.UserBusinessInfo
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule

internal fun BusinessRemote.toDomain(): Business {
    return Business(
        id = id,
        name = name,
        description = description,
        address = address,
        location = location?.let {
            Business.Location(
                lat = it.lat,
                lng = it.lng
            )
        },
        currency = Currency(currencyCode),
        timeZone = timeZone,
        socials = socials.map(BusinessRemote.Social::toDomain).associateBy { it.kind },
        schedule = schedule.toDomain(),
        permissions = permissions.toDomain()
    )
}

internal fun BusinessRemote.Social.toDomain(): Business.Social {
    return Business.Social(
        kind = kind.domainKind,
        value = value
    )
}

internal fun Business.toLocal(): BusinessEntity {
    return BusinessEntity(
        id = id,
        name = name,
        description = description,
        address = address,
        locationLat = location?.lat,
        locationLng = location?.lng,
        currencyCode = currency.code(),
        timeZone = timeZone.id,
        phone = socials[Business.SocialKind.PHONE]?.value,
        insta = socials[Business.SocialKind.INSTAGRAM]?.value,
        viber = socials[Business.SocialKind.VIBER]?.value,
        whatsApp = socials[Business.SocialKind.WHATSAPP]?.value,
        telegram = socials[Business.SocialKind.TELEGRAM]?.value,
        businessPermissionView = permissions.business.view,
        businessPermissionUpdate = permissions.business.update,
        businessPermissionDelete = permissions.business.delete,
        employeesPermissionView = permissions.employees.view,
        employeesPermissionUpdate = permissions.employees.update,
        employeesPermissionDelete = permissions.employees.delete,
        clientsPermissionView = permissions.clients.view,
        clientsPermissionUpdate = permissions.clients.update,
        clientsPermissionDelete = permissions.clients.delete,
        servicesPermissionView = permissions.services.view,
        servicesPermissionUpdate = permissions.services.update,
        servicesPermissionDelete = permissions.services.delete,
        appointmentsPermissionView = permissions.appointments.view,
        appointmentsPermissionUpdate = permissions.appointments.update,
        appointmentsPermissionDelete = permissions.appointments.delete
    )
}

internal fun Business.toDayScheduleEntities() = schedule.days.map { (dayOfWeek, daySchedule) ->
    BusinessDayScheduleEntity(
        businessId = id,
        dayOfWeek = dayOfWeek.name,
        isActive = daySchedule.isActive
    )
}

internal fun Business.toWorkHourEntities() = schedule.days.flatMap { (dayOfWeek, daySchedule) ->
    daySchedule.workingTime.map { workHour ->
        BusinessWorkHourEntity(
            businessId = id,
            dayOfWeek = dayOfWeek.name,
            from = workHour.from.toString(),
            to = workHour.to.toString()
        )
    }
}

internal fun Business.toDayOffEntities() = schedule.dayOffs.map { dayOff ->
    BusinessDayOffEntity(
        businessId = id,
        start = dayOff.start.toString(),
        end = dayOff.end.toString()
    )
}

internal fun BusinessLocal.toDomain(): Business {
    return Business(
        id = entity.id,
        name = entity.name,
        description = entity.description,
        address = entity.address,
        location = entity.locationLat?.let { lat ->
            entity.locationLng?.let { lng -> Business.Location(lat, lng) }
        },
        currency = Currency(entity.currencyCode),
        timeZone = TimeZone.of(entity.timeZone),
        socials = listOf(
            Business.Social(Business.SocialKind.PHONE, entity.phone),
            Business.Social(Business.SocialKind.INSTAGRAM, entity.insta),
            Business.Social(Business.SocialKind.VIBER, entity.viber),
            Business.Social(Business.SocialKind.WHATSAPP, entity.whatsApp),
            Business.Social(Business.SocialKind.TELEGRAM, entity.telegram)
        ).associateBy { it.kind },
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
        permissions = BusinessPermissions(
            business = ResourcePermission(
                view = entity.businessPermissionView,
                update = entity.businessPermissionUpdate,
                delete = entity.businessPermissionDelete
            ),
            employees = ResourcePermission(
                view = entity.employeesPermissionView,
                update = entity.employeesPermissionUpdate,
                delete = entity.employeesPermissionDelete
            ),
            clients = ResourcePermission(
                view = entity.clientsPermissionView,
                update = entity.clientsPermissionUpdate,
                delete = entity.clientsPermissionDelete
            ),
            services = ResourcePermission(
                view = entity.servicesPermissionView,
                update = entity.servicesPermissionUpdate,
                delete = entity.servicesPermissionDelete
            ),
            appointments = ResourcePermission(
                view = entity.appointmentsPermissionView,
                update = entity.appointmentsPermissionUpdate,
                delete = entity.appointmentsPermissionDelete
            )
        )
    )
}

internal fun UserBusinessesRemote.toUserBusinesses(): UserBusinessInfo {
    return UserBusinessInfo(
        dashboardId = dashboardId,
        businesses = businesses.map(BusinessRemote::toDomain)
    )
}

internal fun Business.toRemote(): BusinessUpdateRemote {
    return BusinessUpdateRemote(
        id = id,
        name = name,
        description = description,
        address = address,
        location = location?.let {
            BusinessRemote.Location(
                lat = it.lat,
                lng = it.lng
            )
        },
        currencyCode = currency.code(),
        timeZone = timeZone,
        socials = socials.values.map { it.toRemote() },
        schedule = schedule.toRemote()
    )
}

internal fun Business.Social.toRemote(): BusinessRemote.Social {
    return BusinessRemote.Social(
        kind = BusinessRemote.SocialKind.entries.first { it.domainKind == kind },
        value = value
    )
}
