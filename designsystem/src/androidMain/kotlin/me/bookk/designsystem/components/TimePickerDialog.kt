package me.bookk.designsystem.components

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TimePickerDialog(
//    onCancel: () -> Unit,
//    onConfirm: (LocalTime) -> Unit,
//    modifier: Modifier = Modifier,
//    initial: LocalTime? = null,
//    colors: TimePickerColors = TimePickerDefaults.colors(
//        clockDialColor = AppColors.GrayLow,
//        periodSelectorSelectedContainerColor = AppColors.TextPrimary,
//        periodSelectorUnselectedContainerColor = Color.Transparent,
//        periodSelectorSelectedContentColor = AppColors.White,
//        timeSelectorSelectedContainerColor = AppColors.GrayMedium,
//        timeSelectorUnselectedContainerColor = Color.Transparent,
//        timeSelectorSelectedContentColor = AppColors.TextPrimary
//    ),
//    is24hour: Boolean = false,
//) {
//    var mode: DisplayMode by remember { mutableStateOf(DisplayMode.Picker) }
//    val state: TimePickerState = rememberTimePickerState(
//        initialHour = initial?.hour ?: 0,
//        initialMinute = initial?.minute ?: 0,
//        is24Hour = is24hour
//    )
//
//    // TimePicker does not provide a default TimePickerDialog, so we use our own PickerDialog:
//    // https://issuetracker.google.com/issues/288311426
//    PickerDialog(
//        modifier = modifier,
//        onDismissRequest = onCancel,
//        title = {
//            Text(
//                stringResource(DesignSystem.strings.time_picker_title.resourceId),
//                style = AppTypography.body1SemiBold
//            )
//        },
//        buttons = {
//            DisplayModeToggleButton(
//                displayMode = mode,
//                onDisplayModeChange = { mode = it },
//            )
//            Spacer(Modifier.weight(1f))
//
//            PrimaryButton(
//                state = ButtonStateImpl(DesignSystem.strings.button_cancel.desc()),
//                colors = ButtonDefaults.buttonColors(
//                    contentColor = AppColors.TextQuarternary,
//                    containerColor = Color.Transparent
//                ),
//                onClick = onCancel,
//            )
//            PrimaryButton(
//                modifier = Modifier.padding(end = 8.dp),
//                state = ButtonStateImpl(DesignSystem.strings.button_select.desc()),
//                colors = ButtonDefaults.buttonColors(
//                    contentColor = AppColors.TextPrimary,
//                    containerColor = Color.Transparent
//                ),
//                onClick = { onConfirm(LocalTime(state.hour, state.minute)) }
//            )
//        },
//    ) {
//        val contentModifier = remember { Modifier.padding(horizontal = 24.dp) }
//        when (mode) {
//            DisplayMode.Picker -> TimePicker(
//                modifier = contentModifier,
//                state = state,
//                colors = colors,
//            )
//
//            DisplayMode.Input -> TimeInput(
//                modifier = contentModifier,
//                state = state,
//                colors = colors
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun DisplayModeToggleButton(
//    displayMode: DisplayMode,
//    onDisplayModeChange: (DisplayMode) -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    when (displayMode) {
//        DisplayMode.Picker -> IconButton(
//            modifier = modifier,
//            onClick = { onDisplayModeChange(DisplayMode.Input) },
//        ) {
//            Icon(
//                imageVector = Icons.Filled.Edit,
//                contentDescription = null,
//            )
//        }
//
//        DisplayMode.Input -> IconButton(
//            modifier = modifier,
//            onClick = { onDisplayModeChange(DisplayMode.Picker) },
//        ) {
//            Icon(
//                imageVector = Icons.Filled.DateRange,
//                contentDescription = null,
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PickerDialog(
//    onDismissRequest: () -> Unit,
//    title: @Composable () -> Unit,
//    buttons: @Composable RowScope.() -> Unit,
//    modifier: Modifier = Modifier,
//    content: @Composable ColumnScope.() -> Unit,
//) {
//    BasicAlertDialog(
//        onDismissRequest = onDismissRequest,
//        modifier = modifier
//            .width(IntrinsicSize.Min)
//            .height(IntrinsicSize.Min),
//        properties = DialogProperties(usePlatformDefaultWidth = false)
//    ) {
//        Surface(
//            shape = MaterialTheme.shapes.medium,
//            color = AppColors.White
//        ) {
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.Start)
//                        .padding(horizontal = 24.dp)
//                        .padding(top = 16.dp, bottom = 20.dp),
//                ) {
//                    title()
//                }
//
//                content()
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 8.dp, end = 6.dp, start = 6.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
//                ) {
//                    buttons()
//                }
//            }
//        }
//    }
//}