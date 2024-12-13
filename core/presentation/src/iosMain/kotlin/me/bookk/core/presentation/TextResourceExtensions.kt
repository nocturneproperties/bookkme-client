package me.bookk.core.presentation

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.core.UsedInSwift

@UsedInSwift
fun StringDesc.string(): String {
    return this.localized()
}