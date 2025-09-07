package me.bookk.feature.business.presentation.settings.state

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.PickerPresentation

data class CurrencyUI(
    override val pickerItemId: Long,
    override val displayName: StringDesc,
    val domainValue: String
) : PickerPresentation()