package me.bookk.client.di.feature

import me.bookk.feature.booking.data.di.bookingDataModule
import me.bookk.feature.booking.domain.impl.di.bookingDomainModule
import me.bookk.feature.booking.presentation.di.bookingPresentationModule
import org.koin.dsl.module

internal fun bookingDiModule() = module {
    includes(
        bookingPresentationModule(),
        bookingDomainModule(),
        bookingDataModule(),
    )
}
