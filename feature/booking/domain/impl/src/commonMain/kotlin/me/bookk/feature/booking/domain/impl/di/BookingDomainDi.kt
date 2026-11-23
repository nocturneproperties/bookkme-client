package me.bookk.feature.booking.domain.impl.di

import me.bookk.feature.booking.domain.api.GetBookingCatalog
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.SetBookingServices
import me.bookk.feature.booking.domain.impl.GetBookingCatalogImpl
import me.bookk.feature.booking.domain.impl.GetBookingPreparationImpl
import me.bookk.feature.booking.domain.impl.SetBookingServicesImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun bookingDomainModule(): Module {
    return module {
        factoryOf(::GetBookingCatalogImpl) bind GetBookingCatalog::class
        factoryOf(::GetBookingPreparationImpl) bind GetBookingPreparation::class
        factoryOf(::SetBookingServicesImpl) bind SetBookingServices::class
    }
}
