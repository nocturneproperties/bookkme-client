package me.bookk.designsystem.components

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DatePickerField(
//    modifier: Modifier = Modifier,
//    title: StringDesc,
//    fieldState: TextFieldState,
//    trailingIcon: @Composable (() -> Unit)? = null,
//    leadingIcon: @Composable (() -> Unit)? = null,
//    onDatePicked: (LocalDate) -> Unit,
//    maxSelectableDate: LocalDate? = null,
//    minSelectableDate: LocalDate? = null,
//    fieldColors: TextFieldColors = defaultOutlinedTextFieldColors(),
//    confirmButton: ButtonState = ButtonStateImpl(DesignSystem.strings.button_select.desc()),
//    dismissButton: ButtonState? = ButtonStateImpl(DesignSystem.strings.button_cancel.desc()),
//    datePickerColors: DatePickerColors = DatePickerDefaults.colors(
//        containerColor = AppColors.White,
//        dateTextFieldColors = defaultTextFieldColors()
//    ),
//    titleColor: Color = AppColors.TextTertiary,
//) {
//    val datePickerState = rememberDatePickerState(
//        initialSelectedDateMillis = System.currentTimeMillis(),
//        selectableDates = SelectableDateRange(maxSelectableDate, minSelectableDate)
//    )
//    val showDatePicker = remember { mutableStateOf(false) }
//
//    TitledTextField(
//        modifier = modifier,
//        interactionSource = SingleClickInteractionSource {
//            showDatePicker.value = true
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
//    if (showDatePicker.value) {
//        DatePickerDialog(
//            colors = datePickerColors,
//            onDismissRequest = { showDatePicker.value = false },
//            dismissButton = dismissButton?.let { dismissButtonState ->
//                {
//                    PrimaryButton(
//                        state = dismissButtonState,
//                        colors = ButtonDefaults.buttonColors(
//                            contentColor = AppColors.TextQuarternary,
//                            containerColor = Color.Transparent
//                        ),
//                        onClick = { showDatePicker.value = false },
//                    )
//                }
//            },
//            confirmButton = {
//                PrimaryButton(
//                    modifier = Modifier.padding(end = 8.dp),
//                    state = confirmButton,
//                    colors = ButtonDefaults.buttonColors(
//                        contentColor = AppColors.TextPrimary,
//                        containerColor = Color.Transparent
//                    ),
//                    onClick = {
//                        datePickerState.selectedDateMillis?.let { timestamp ->
//                            val date = Instant.fromEpochMilliseconds(timestamp)
//                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
//                            onDatePicked(date)
//                        }
//                        showDatePicker.value = false
//                    }
//                )
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
//}
