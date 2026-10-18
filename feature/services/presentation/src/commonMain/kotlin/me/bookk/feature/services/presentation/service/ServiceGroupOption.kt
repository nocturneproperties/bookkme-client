package me.bookk.feature.services.presentation.service

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.PickerPresentation
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup

data class ServiceGroupOption(
    val domain: ServiceGroup
) : PickerPresentation() {
    override val pickerItemId: String = domain.id.toString()
    override val displayName: StringDesc = domain.name.desc()
}
