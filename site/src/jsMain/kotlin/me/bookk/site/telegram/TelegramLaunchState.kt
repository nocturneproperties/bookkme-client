package me.bookk.site.telegram

sealed interface TelegramLaunchState {
    data object Loading : TelegramLaunchState

    data object OutsideTelegram : TelegramLaunchState

    class InsideTelegram(
        val webApp: TelegramWebApp,
    ) : TelegramLaunchState
}
