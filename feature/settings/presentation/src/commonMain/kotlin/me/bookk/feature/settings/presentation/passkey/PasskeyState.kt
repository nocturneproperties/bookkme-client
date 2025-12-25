package me.bookk.feature.settings.presentation.passkey

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import kotlin.uuid.Uuid

interface PasskeyState {

    val appBar: AppBarState
    val addPasskeyButton: ButtonState
    val passkeys: List<PasskeyItem>
    val refresh: RefreshState

    val notification: PresentationNotificationState

    /**
     * Modification directly from passkeys field (if we change type to MutableList) brings complications
     * on Swift side because of Kotlin/ObjC interop. Mappings are:
     *
     * List -> Array (SwiftUI @Publish available on every item change)
     * MutableList -> NSMutableArray (Unnecessary complicated and not Swift-friendly)
     */
    fun replacePasskeyList(items: List<PasskeyItem>)

    data class PasskeyItem(
        val id: Uuid,
        val title: String,
        val isDeletable: Boolean,
        val isBackedUp: Boolean,
        val addedOn: StringDesc
    )

    class InitData(
        val title: StringDesc,
        val addButtonText: StringDesc
    )
}