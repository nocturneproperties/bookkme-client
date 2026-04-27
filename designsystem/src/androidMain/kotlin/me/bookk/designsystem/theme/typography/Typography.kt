package me.bookk.designsystem.theme.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import me.bookk.designsystem.theme.color.LocalColors

@Composable
fun TextStyle.primary() = copy(color = LocalColors.current.primaryText)

@Composable
fun TextStyle.secondary() = copy(color = LocalColors.current.secondaryText)

@Composable
fun TextStyle.active() = copy(color = LocalColors.current.actionText)

@Composable
fun TextStyle.error() = copy(color = LocalColors.current.error)