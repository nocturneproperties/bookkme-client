package me.bookk.feature.authorization.presentation.sign_in.state

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

class TroubleshootCardData(
    val title: StringDesc,
    val icon: ImageResource,
    val reasons: List<Reason>
) {
    class Reason(
        val id: Int,
        val title: StringDesc,
        val description: StringDesc
    )
}