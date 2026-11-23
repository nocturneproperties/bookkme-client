package me.bookk.feature.booking.presentation.di

import me.bookk.feature.booking.presentation.BookingStateFactory
import me.bookk.feature.booking.presentation.factory.WebBookingStateFactory
import me.bookk.feature.booking.presentation.screen.services.BookingServicesViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun bookingPresentationModule(): Module {
    return module {
        factoryOf(::WebBookingStateFactory) bind BookingStateFactory::class
        factoryOf(::BookingServicesViewModel)
    }
}
