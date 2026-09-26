package me.bookk.feature.employees.presentation.screen.edit

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.PickerPresentation
import me.bookk.feature.services.domain.api.service.entity.Service

data class EmployeeServicePresentation(
    override val pickerItemId: String,
    override val displayName: StringDesc,
    val duration: StringDesc,
    val price: String,
    val service: Service
) : PickerPresentation() {

    constructor(service: Service) : this(
        pickerItemId = service.id.toString(),
        displayName = service.name.desc(),
        duration = service.duration.toString().desc(),
        price = service.price.toString(),
        service = service
    )
}
