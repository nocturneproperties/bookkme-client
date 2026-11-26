package me.bookk.feature.settings.presentation

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.test.FakeAppBarState
import me.bookk.designsystem.test.FakeButtonState
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.feature.settings.presentation.confirmaction.ConfirmActionDestinations
import me.bookk.feature.settings.presentation.confirmaction.ConfirmActionState

internal class FakeClientSettingsStateFactory : ClientSettingsStateFactory {
    override fun createConfirmActionState(): ConfirmActionState {
        return FakeConfirmActionState()
    }
}

internal class FakeConfirmActionState : ConfirmActionState {
    override val appBar = FakeAppBarState()
    override var icon: ImageResource? = null
    override var iconColor: ColorToken = ColorToken.ActionText
    override var message: StringDesc = "".desc()
    override var error: StringDesc? = null
    override var isDestructive: Boolean = false
    override val mainButton = FakeButtonState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<ConfirmActionDestinations>()
}
