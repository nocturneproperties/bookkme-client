package me.bookk.feature.settings.presentation.export

import dev.icerock.moko.resources.StringResource
import me.bookk.android.feature.settings.resources.SettingsRes

internal actual val exportRunningFooter: StringResource
    get() {
        return SettingsRes.strings.settings_export_running_footer_mini_app
    }

internal actual val exportConfirmationError: StringResource
    get() {
        return SettingsRes.strings.settings_export_confirmation_unavailable_mini_app
    }
