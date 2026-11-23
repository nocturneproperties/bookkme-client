package me.bookk.feature.booking.presentation

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.test.FakeAppBarState
import me.bookk.designsystem.test.FakeButtonState
import me.bookk.designsystem.test.FakeListState
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.designsystem.test.FakeOptionsMultiPickerState
import me.bookk.feature.booking.presentation.screen.services.BookingServicesDestinations
import me.bookk.feature.booking.presentation.screen.services.BookingServicesState
import me.bookk.feature.booking.presentation.screen.services.ServiceGroupItem
import me.bookk.feature.booking.presentation.screen.services.ServiceOption
import me.bookk.feature.booking.presentation.screen.services.ServicesTotal
import me.bookk.feature.booking.presentation.screen.specialist.BookingSpecialistDestinations
import me.bookk.feature.booking.presentation.screen.specialist.BookingSpecialistState
import me.bookk.feature.booking.presentation.screen.specialist.SpecialistItem

class FakeBookingStateFactory : BookingStateFactory {
    override fun createBookingServicesState(): BookingServicesState {
        return FakeBookingServicesState()
    }

    override fun createBookingSpecialistState(): BookingSpecialistState {
        return FakeBookingSpecialistState()
    }
}

class FakeBookingServicesState : BookingServicesState {
    override val appBar = FakeAppBarState()
    override var businessName: StringDesc? = null
    override val groups = FakeListState<ServiceGroupItem>()
    override val services = FakeOptionsMultiPickerState<ServiceOption>()
    override var total: ServicesTotal? = null
    override val continueButton = FakeButtonState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<BookingServicesDestinations>()
}

class FakeBookingSpecialistState : BookingSpecialistState {
    override val appBar = FakeAppBarState()
    override var summary: StringDesc? = null
    override val specialists = FakeListState<SpecialistItem>()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<BookingSpecialistDestinations>()
}
