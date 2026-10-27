package me.bookk.core.presentation

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.StringDesc

fun PluralsResource.plural(quantity: Int): StringDesc {
    return StringDesc.Plural(this, quantity)
}

@Composable
internal fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}