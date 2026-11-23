package me.bookk.feature.booking.data.di

import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.booking.data.datasource.InMemoryBookingPreparationDataSource
import me.bookk.feature.booking.domain.datasource.BookingPreparationDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

fun bookingDataModule(): Module {
    return module {
        singleOf(::InMemoryBookingPreparationDataSource) binds
            arrayOf(BookingPreparationDataSource::class, LogOutAction::class)
    }
}
