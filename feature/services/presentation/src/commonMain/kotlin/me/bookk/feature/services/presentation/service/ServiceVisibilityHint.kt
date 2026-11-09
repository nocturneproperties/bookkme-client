package me.bookk.feature.services.presentation.service

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.services.resources.ServicesRes

internal fun serviceVisibilityHint(isVisible: Boolean): StringDesc {
    val hint = if (isVisible) {
        ServicesRes.strings.services_create_visible_on_hint
    } else {
        ServicesRes.strings.services_create_visible_off_hint
    }
    return hint.desc()
}
