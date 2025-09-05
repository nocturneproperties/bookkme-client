package me.bookk.feature.settings.presentation.passkey

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.DispatcherProvider
import me.bookk.core.UsedInSwift
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.feature.settings.domain.api.CreateNewPasskey
import me.bookk.feature.settings.domain.api.DeletePasskey
import me.bookk.feature.settings.domain.api.GetAvailablePasskeys
import me.bookk.feature.settings.domain.api.entity.Passkey
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.passkey.PasskeyState.PasskeyItem
import kotlin.uuid.Uuid

class PasskeyViewModel(
    private val deletePasskey: DeletePasskey,
    private val createNewPasskey: CreateNewPasskey,
    private val getAvailablePasskeys: GetAvailablePasskeys,
    private val dateLocalizer: DateLocalizer,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = settingsStateFactory.createPasskeyState(createInitData())

    init {
        getPasskeyList()
    }

    fun onAddPasskeyClick() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.addPasskeyButton.isLoading = true
                uiState.addPasskeyButton.isEnabled = false
            },
            call = {
                createNewPasskey()
                getAvailablePasskeys()
            },
            onComplete = ::updatePasskeyList,
            onTerminate = {
                uiState.addPasskeyButton.isLoading = false
                uiState.addPasskeyButton.isEnabled = true
            },
            onError = {
                uiState.notification.add(errorMapper.mapToNotification(it))
            }
        )
    }

    fun onDeletePasskeyClick(passkeyItem: PasskeyItem) {
        uiState.notification.add(
            PresentationNotification.Message(
                title = SettingsRes.strings.settings_passkey_delete_dialog_title.desc(),
                message = SettingsRes.strings.settings_passkey_delete_dialog_message.format(
                    passkeyItem.title
                ),
                buttons = listOf(
                    ButtonDescriptor(
                        text = DesignSystem.strings.action_cancel.desc(),
                    ),
                    ButtonDescriptor(
                        text = DesignSystem.strings.action_confirm.desc(),
                        actionType = ButtonDescriptor.ActionType.NEGATIVE,
                        onClick = { deletePasskeyById(passkeyItem.id) }
                    )
                )
            )
        )
    }

    @UsedInSwift
    fun onDeletePasskeyList(itemList: List<PasskeyItem>) {
        uiState.notification.add(
            PresentationNotification.Message(
                title = SettingsRes.strings.settings_passkey_delete_dialog_title.desc(),
                message = SettingsRes.strings.settings_passkey_delete_dialog_message.format(
                    itemList.joinToString { it.title }
                ),
                buttons = listOf(
                    ButtonDescriptor(
                        text = DesignSystem.strings.action_cancel.desc(),
                    ),
                    ButtonDescriptor(
                        text = DesignSystem.strings.action_confirm.desc(),
                        actionType = ButtonDescriptor.ActionType.NEGATIVE,
                        onClick = { deletePasskeyByIdList(itemList.map { it.id }) }
                    )
                )
            )
        )
    }

    fun getPasskeyList() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.refresh.isRefreshing = true },
            call = { getAvailablePasskeys() },
            onComplete = ::updatePasskeyList,
            onError = { uiState.notification.add(errorMapper.mapToNotification(it)) },
            onTerminate = { uiState.refresh.isRefreshing = false },
        )
    }

    private fun deletePasskeyById(id: Uuid) {
        launch(
            launchIn = DispatcherProvider.io,
            call = {
                deletePasskey(id)
                getAvailablePasskeys()
            },
            onComplete = ::updatePasskeyList,
            onError = { uiState.notification.add(errorMapper.mapToNotification(it)) }
        )
    }

    private fun deletePasskeyByIdList(ids: List<Uuid>) {
        launch(
            launchIn = DispatcherProvider.io,
            call = {
                ids.forEach { id ->
                    deletePasskey(id)
                }
                getAvailablePasskeys()
            },
            onComplete = ::updatePasskeyList,
            onError = { uiState.notification.add(errorMapper.mapToNotification(it)) }
        )
    }

    private fun updatePasskeyList(passkeys: List<Passkey>) {
        uiState.replacePasskeyList(passkeys.map { it.toViewItem(passkeys.size) })
    }

    private fun Passkey.toViewItem(totalItemCount: Int): PasskeyItem {
        return PasskeyItem(
            id = id,
            title = name,
            isBackedUp = isBackedUp,
            isDeletable = totalItemCount > 1,
            addedOn = SettingsRes.strings.settings_passkey_added_on_format.format(
                dateLocalizer.format(createdAt.date, DateLocalizer.Style.MEDIUM)
            )
        )
    }

    companion object {
        fun createInitData(): PasskeyState.InitData {
            return PasskeyState.InitData(
                title = SettingsRes.strings.settings_passkey_title.desc(),
                addButtonText = SettingsRes.strings.settings_passkey_add_button.desc(),
            )
        }
    }
}