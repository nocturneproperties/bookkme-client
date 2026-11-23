package me.bookk.feature.authorization.presentation.screen.telegram

import androidx.compose.runtime.Composable
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.rememberViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TelegramSignInDestination(
    webApp: TelegramWebApp,
    messageLayout: @Composable (content: @Composable () -> Unit) -> Unit,
    formLayout: @Composable (content: @Composable () -> Unit) -> Unit,
) {
    val viewModel = rememberViewModel<TelegramSignInViewModel>(webApp.initData) {
        parametersOf(webApp.initData)
    }
    TelegramSignInScreen(viewModel.uiState, webApp, messageLayout, formLayout)
}
