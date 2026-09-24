package me.bookk.designsystem.test

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import library.money.api.Money
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.navigation.NavigationDestination
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.BusinessMenuItem
import me.bookk.designsystem.uistate.BusinessMenuState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.DatePickerFieldState
import me.bookk.designsystem.uistate.DatePickerState
import me.bookk.designsystem.uistate.DateRangePickerState
import me.bookk.designsystem.uistate.DateTimePickerState
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.MoneyFieldState
import me.bookk.designsystem.uistate.MultiPickerState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PickerPresentation
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RadioButtonState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.designsystem.uistate.TextState
import me.bookk.designsystem.uistate.TimePickerFieldState
import me.bookk.designsystem.uistate.TimePickerState
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.designsystem.uistate.simple.BannerErrorState
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.designsystem.uistate.simple.ErrorState
import me.bookk.designsystem.uistate.simple.TextIcon
import kotlin.uuid.Uuid

class FakeListState<T>(initialItems: List<T> = emptyList()) : FakeViewState(), ListState<T> {
    override val items: MutableList<T> = initialItems.toMutableList()
    override var loadMore: (() -> Unit)? = null
    override var emptyState: EmptyState? = null
    override var errorState: ErrorState? = null
    override var bannerError: BannerErrorState? = null
    override var isInitialLoading: Boolean = true

    override fun append(list: List<T>) {
        items += list
        isInitialLoading = false
    }

    override fun replace(list: List<T>) {
        items.clear()
        items += list
        isInitialLoading = false
    }

    override fun clear() {
        items.clear()
    }
}

class FakeAppBarState : FakeViewState(), AppBarState {
    override var title: StringDesc = "".desc()
    override var subtitle: StringDesc? = null
    override var onBackClick: (() -> Unit)? = null
    override var size: TopBarSize = TopBarSize.SMALL
    override val actions: FakeListState<AppBarAction> = FakeListState()
}

class FakeButtonState : FakeViewState(), ButtonState {
    override var icon: ImageResource? = null
    override var text: StringDesc = "".desc()
    override var isLoading: Boolean = false
    override var isEnabled: Boolean = true
    override var onClick: (() -> Unit)? = null
}

class FakeTextState : FakeViewState(), TextState {
    override var text: StringDesc = "".desc()
    override var isHighlighted: Boolean = false
}

class FakeBooleanState : FakeViewState(), BooleanState {
    override var text: StringDesc = "".desc()
    override var isEnabled: Boolean = true
    override var isChecked: Boolean = false
    override var isValid: Boolean = true
    override var validationState: ValidationState = ValidationState.DEFAULT
    override var supportingTextRes: StringDesc? = null
    override var onCheckedChange: ((Boolean) -> Unit)? = null
}

class FakeRadioButtonState : FakeViewState(), RadioButtonState {
    override var text: StringDesc = "".desc()
    override var isEnabled: Boolean = true
    override var isSelected: Boolean = false
    override var onClick: (() -> Unit)? = null
}

class FakeTextFieldState : FakeViewState(), TextFieldState {
    override var placeholder: StringDesc = "".desc()
    override var label: StringDesc = "".desc()
    override var text: String = ""
    override var suffix: StringDesc? = null
    override var startIcon: ImageResource? = null
    override var endIcon: ImageResource? = null
    override var supportingTextRes: StringDesc? = null
    override var validationState: ValidationState = ValidationState.DEFAULT
    override var inputType: InputType = InputType.TEXT
    override var isValid: Boolean = true
    override var enabled: Boolean = true
    override var readOnly: Boolean = false
    override var maxLength: Int = Int.MAX_VALUE
    override var onTextChanged: ((String) -> Unit)? = null
    var lastUpdatedDesc: StringDesc? = null

    override fun updateText(desc: StringDesc?) {
        lastUpdatedDesc = desc
    }

    fun type(value: String) {
        text = value
        onTextChanged?.invoke(value)
    }
}

class FakeDatePickerState : DatePickerState {
    override var isDatePickerVisible: Boolean = false
    override var pickedDate: LocalDate? = null
    override var maxDate: LocalDate? = null
    override var minDate: LocalDate? = null
    override var onDatePicked: ((LocalDate) -> Unit)? = null
}

class FakeDatePickerFieldState : FakeViewState(), DatePickerFieldState {
    override val textField: FakeTextFieldState = FakeTextFieldState()
    override val datePicker: FakeDatePickerState = FakeDatePickerState()
}

class FakeDateRangePickerState : FakeViewState(), DateRangePickerState {
    override var title: StringDesc = "".desc()
    override val startDate: FakeDatePickerFieldState = FakeDatePickerFieldState()
    override val endDate: FakeDatePickerFieldState = FakeDatePickerFieldState()
    override var onDateRangeSelected: () -> Unit = {}
}

class FakeDateTimePickerState : DateTimePickerState {
    override var isDatePickerVisible: Boolean = false
    override var pickedDate: LocalDateTime? = null
    override var maxDate: LocalDateTime? = null
    override var minDate: LocalDateTime? = null
    override var onDatePicked: ((LocalDateTime) -> Unit)? = null
}

class FakeTimePickerState : TimePickerState {
    override var isTimePickerVisible: Boolean = false
    override var maxTime: LocalTime? = null
    override var minTime: LocalTime? = null
    override var pickedTime: LocalTime? = null
    override var onTimePicked: ((LocalTime) -> Unit)? = null
}

class FakeTimePickerFieldState : FakeViewState(), TimePickerFieldState {
    override val textField: FakeTextFieldState = FakeTextFieldState()
    override val timePicker: FakeTimePickerState = FakeTimePickerState()
}

class FakeMoneyFieldState : FakeViewState(), MoneyFieldState {
    override var currentValue: Money = Money(0L, Money.SupportedCurrency.UAH)
    override val textState: FakeTextFieldState = FakeTextFieldState()
    override var currency: TextIcon? = null
}

class FakePickerFieldState<T : PickerPresentation> : FakeViewState(), PickerFieldState<T> {
    override val textField: FakeTextFieldState = FakeTextFieldState()
    override var pickerType: PickerFieldState.PickerType = PickerFieldState.PickerType.BOTTOM_SHEET
    override var pickerTitle: StringDesc = "".desc()
    override val options: MutableList<T> = mutableListOf()
    override var selectedItem: T? = null
    override var onItemPicked: (T?) -> Unit = {}

    override fun replaceOptions(options: List<T>) {
        this.options.clear()
        this.options += options
    }

    fun pick(item: T?) {
        selectedItem = item
        onItemPicked(item)
    }
}

class FakeMultiPickerState<T : PickerPresentation> : FakeViewState(), MultiPickerState<T> {
    override var pickerTitle: StringDesc = "".desc()
    override var placeholder: StringDesc? = null
    override val selectedItems: MutableList<T> = mutableListOf()
    override var onItemsPicked: (List<T>) -> Unit = {}
    override var onItemsRemoveRequested: (List<T>) -> Unit = {}
    override var addItemButton: ButtonState = FakeButtonState()
    override var isEditable: Boolean = true
    override var isPickerVisible: Boolean = false

    override fun replaceSelected(items: List<T>) {
        selectedItems.clear()
        selectedItems += items
    }
}

class FakeOptionsMultiPickerState<T : PickerPresentation> : FakeViewState(), OptionsMultiPickerState<T> {
    override var pickerTitle: StringDesc = "".desc()
    override val options: MutableList<T> = mutableListOf()
    override val selectedItems: MutableList<T> = mutableListOf()
    override var onItemsPicked: (List<T>) -> Unit = {}
    override var onItemsRemoveRequested: (List<T>) -> Unit = {}
    override var addItemText: StringDesc = "".desc()
    override var isEditable: Boolean = true

    override fun replaceOptions(options: List<T>) {
        this.options.clear()
        this.options += options
    }

    override fun replaceSelected(selected: List<T>) {
        selectedItems.clear()
        selectedItems += selected
    }
}

class FakeRefreshState : RefreshState {
    override var isRefreshing: Boolean = false
    override var onRefresh: () -> Unit = {}
}

class FakeBusinessMenuState : BusinessMenuState {
    override var items: List<BusinessMenuItem> = emptyList()
    override var selectedBusinessId: Uuid? = null
    override var onBusinessClick: ((Uuid) -> Unit)? = null
    override var onCreateClick: (() -> Unit)? = null
    override var onJoinClick: (() -> Unit)? = null
}

class FakeNotificationState : PresentationNotificationState {
    override val presentationNotification: MutableList<PresentationNotification> = mutableListOf()

    override fun add(notification: PresentationNotification) {
        presentationNotification += notification
    }

    override fun removeFirst() {
        presentationNotification.removeAt(0)
    }
}

class FakeNavigationState<T : NavigationDestination> : NavigationState<T> {
    override val navigationDestination: MutableList<T> = mutableListOf()

    override fun push(destination: T) {
        navigationDestination += destination
    }

    override fun removeFirst() {
        navigationDestination.removeAt(0)
    }
}
