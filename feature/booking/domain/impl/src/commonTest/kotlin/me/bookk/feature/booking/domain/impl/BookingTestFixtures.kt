package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import library.money.api.Money
import me.bookk.feature.booking.domain.api.entity.BookingService
import me.bookk.feature.booking.domain.datasource.BookingPreparationDataSource
import me.bookk.feature.booking.domain.datasource.BookingSelection
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal val STUB_APPROVED_PRICE = Money(1_000, Money.SupportedCurrency.UAH)

internal class FakeBookingPreparationDataSource(
    private val approvedPrices: Map<Uuid, Money> = emptyMap(),
) : BookingPreparationDataSource {
    private val selections = MutableStateFlow<Map<Uuid, BookingSelection>>(emptyMap())

    fun selectionOf(businessId: Uuid): BookingSelection {
        return selections.value[businessId] ?: BookingSelection()
    }

    fun select(
        businessId: Uuid,
        vararg services: Pair<Uuid, Int>,
        specialistId: Uuid? = null,
        slotStart: Instant? = null,
    ) {
        val selected = services.map { (id, quantity) ->
            BookingSelection.SelectedService(
                id,
                quantity,
                approvedPrices[id] ?: STUB_APPROVED_PRICE,
            )
        }
        val selection = BookingSelection(selected, specialistId, slotStart)
        selections.update { it + (businessId to selection) }
    }

    override fun observeSelection(businessId: Uuid): Flow<BookingSelection> {
        return selections.map { it[businessId] ?: BookingSelection() }
    }

    override suspend fun saveSelection(businessId: Uuid, selection: BookingSelection) {
        selections.update { it + (businessId to selection) }
    }
}

internal fun stubGroup(businessId: Uuid, name: String, createdAtMs: Long): ServiceGroup {
    return ServiceGroup(
        id = Uuid.random(),
        businessId = businessId,
        name = name,
        createdAt = Instant.fromEpochMilliseconds(createdAtMs),
    )
}

internal fun stubService(
    group: ServiceGroup,
    name: String,
    duration: Duration,
    priceCents: Long,
    isAvailable: Boolean = true,
): Service {
    return Service(
        id = Uuid.random(),
        businessId = group.businessId,
        group = group,
        name = name,
        duration = duration,
        price = Money(priceCents, Money.SupportedCurrency.UAH),
        isAvailable = isAvailable,
        createdAt = Instant.fromEpochMilliseconds(0),
    )
}

internal fun approvedPricesOf(vararg services: Service): Map<Uuid, Money> {
    return services.associate { it.id to it.price }
}

internal fun Service.toExpectedBookingService(): BookingService {
    return BookingService(id = id, name = name, duration = duration, price = price)
}

internal fun stubBusiness(
    id: Uuid,
    name: String,
    timeZone: TimeZone = TimeZone.UTC,
    schedule: WorkingSchedule = WorkingSchedule(),
): Business {
    return Business(
        id = id,
        name = name,
        description = "",
        address = "",
        location = null,
        currency = Currency("UAH"),
        timeZone = timeZone,
        socials = emptyMap(),
        schedule = schedule,
        permissions = BusinessPermissions(
            business = ResourcePermission(),
            employees = ResourcePermission(),
            clients = ResourcePermission(),
            services = ResourcePermission(),
            appointments = ResourcePermission(),
        ),
    )
}

internal fun stubEmployee(
    businessId: Uuid,
    name: String,
    lastName: String,
    services: List<Service>,
): BookableEmployee {
    return BookableEmployee(
        id = Uuid.random(),
        businessId = businessId,
        name = name,
        lastName = lastName,
        services = services,
        schedule = WorkingSchedule(),
    )
}
