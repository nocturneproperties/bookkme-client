package me.bookk.designsystem.components

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DateTimePickerField(
//    modifier: Modifier = Modifier,
//    title: StringDesc,
//    fieldState: TextFieldState,
//    trailingIcon: @Composable (() -> Unit)? = null,
//    leadingIcon: @Composable (() -> Unit)? = null,
//    onDateTimePicked: (LocalDateTime) -> Unit,
//    maxSelectableDateTime: LocalDateTime? = null,
//    minSelectableDateTime: LocalDateTime? = null,
//    fieldColors: TextFieldColors = defaultOutlinedTextFieldColors(),
//    selectTimeButton: ButtonState = ButtonStateImpl(DesignSystem.strings.time_picker_title.desc()),
//    dismissButton: ButtonState? = ButtonStateImpl(DesignSystem.strings.button_cancel.desc()),
//    datePickerColors: DatePickerColors = DatePickerDefaults.colors(
//        containerColor = AppColors.White,
//        dateTextFieldColors = defaultTextFieldColors()
//    ),
//    titleColor: Color = AppColors.TextTertiary,
//) {
//    var showDatePicker by remember { mutableStateOf(false) }
//    var showTimePicker by remember { mutableStateOf(false) }
//
//    val selectableDateRange = remember {
//        SelectableDateRange(
//            maxSelectableDateTime?.date,
//            minSelectableDateTime?.date
//        )
//    }
//    var selectedDateMillis by remember {
//        mutableStateOf(selectableDateRange.getInitialOrClosestAvailable(LocalDate.now()))
//    }
//    var selectedTime by remember {
//        mutableStateOf(LocalDateTime.now().time)
//    }
//
//    val datePickerState = rememberDatePickerState(
//        initialSelectedDateMillis = selectedDateMillis,
//        selectableDates = selectableDateRange
//    )
//
//    fun onDateTimeSelected() {
//        val selectedDate = Instant.fromEpochMilliseconds(selectedDateMillis)
//            .toLocalDateTime(TimeZone.currentSystemDefault()).date
//
//        onDateTimePicked(
//            LocalDateTime(selectedDate, selectedTime)
//        )
//    }
//
//    TitledTextField(
//        modifier = modifier,
//        interactionSource = SingleClickInteractionSource {
//            showDatePicker = true
//        },
//        title = title,
//        state = fieldState,
//        trailingIcon = trailingIcon,
//        leadingIcon = leadingIcon,
//        colors = fieldColors,
//        titleColor = titleColor,
//        onValueChange = {}
//    )
//
//    if (showDatePicker) {
//        DatePickerDialog(
//            colors = datePickerColors,
//            onDismissRequest = { showDatePicker = false },
//            dismissButton = dismissButton?.let { dismissButtonState ->
//                {
//                    PrimaryButton(
//                        state = dismissButtonState,
//                        colors = ButtonDefaults.buttonColors(
//                            contentColor = AppColors.TextQuarternary,
//                            containerColor = Color.Transparent
//                        ),
//                        onClick = { showDatePicker = false },
//                    )
//                }
//            },
//            confirmButton = {
//                PrimaryButton(
//                    modifier = Modifier.padding(end = 8.dp),
//                    state = selectTimeButton,
//                    colors = ButtonDefaults.buttonColors(
//                        contentColor = AppColors.TextPrimary,
//                        containerColor = Color.Transparent
//                    ),
//                    onClick = {
//                        datePickerState.selectedDateMillis?.let {
//                            selectedDateMillis = it
//                        }
//                        showDatePicker = false
//                        showTimePicker = true
//                    }
//                )
//            }
//        ) {
//            DatePicker(
//                state = datePickerState,
//                colors = datePickerColors
//            )
//        }
//    }
//
//    if (showTimePicker) {
//        TimePickerDialog(
//            onCancel = { showTimePicker = false },
//            onConfirm = { localTime ->
//                selectedTime = localTime
//                showTimePicker = false
//                onDateTimeSelected()
//            },
//            initial = selectedTime
//        )
//    }
//}
//
///**
// * Calculates the initial selected date in UTC milliseconds.
// *
// * @param initial The initial date to consider.
// * @return The UTC milliseconds representation of the initial date if it's selectable,
// * or the UTC milliseconds of the closest available date within the range.
// * @throws IllegalStateException if no selectable date is found within the range.
// */
//fun SelectableDateRange.getInitialOrClosestAvailable(initial: LocalDate): Long {
//    val initialMillis = initial.toUTCMillis()
//    return if (isSelectableDate(initialMillis)) {
//        initialMillis
//    } else {
//        min?.toUTCMillis() ?: max?.toUTCMillis()
//        ?: throw IllegalStateException("Unsupported date range: $this")
//    }
//}
