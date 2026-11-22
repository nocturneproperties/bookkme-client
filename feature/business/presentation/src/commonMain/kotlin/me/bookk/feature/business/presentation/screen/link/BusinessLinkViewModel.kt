package me.bookk.feature.business.presentation.screen.link

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.flowOn
import library.device.api.DeviceFacade
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.config.SiteUrl
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.domain.entity.Error
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.clearError
import me.bookk.designsystem.uistate.showError
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.business.domain.api.entity.BusinessLink
import me.bookk.feature.business.domain.api.link.GetBusinessLink
import me.bookk.feature.business.domain.api.link.SetBusinessLink
import me.bookk.feature.business.presentation.BusinessStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class BusinessLinkViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getBusinessLink: GetBusinessLink,
    private val setBusinessLink: SetBusinessLink,
    private val device: DeviceFacade,
    private val siteUrl: SiteUrl,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: BusinessLinkState = stateFactory.createBusinessLinkState().setup()
    private var savedName = ""

    init {
        observeLink()
        refreshLink()
    }

    private fun BusinessLinkState.setup() = apply {
        appBar.title = BusinessRes.strings.business_link_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(BusinessLinkDestinations.Back)
        }
        name.placeholder = BusinessRes.strings.business_link_name_placeholder.desc()
        name.inputType = InputType.ASCII
        name.onTextChanged = weakVMClosure { vm, text -> vm.onNameChanged(text) }
        preview = previewOf("")
        save.isEnabled = false
        refresh.onRefresh = weakVMClosure { it.refreshLink() }
        save.text = DesignSystem.strings.action_save.desc()
        save.onClick = weakVMClosure { it.saveLink() }
        share.text = BusinessRes.strings.business_link_share.desc()
        share.isVisible = false
        share.onClick = weakVMClosure { it.shareLink() }
    }

    private fun observeLink() {
        getBusinessLink.flow(businessId)
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderLink(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun refreshLink() {
        launch(
            key = REFRESH_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.loadError = null
                uiState.refresh.isRefreshing = true
            },
            call = { getBusinessLink.refresh(businessId) },
            onError = { handleRefreshError(it) },
            onTerminate = { uiState.refresh.isRefreshing = false },
        )
    }

    private fun handleRefreshError(error: Throwable) {
        when (error) {
            is Error.Ignore, is CancellationException -> {
            }

            is Error.Unauthorized, is Error.BusinessAccessSuspended -> {
                uiState.notifications.add(error.notification())
            }

            is GetBusinessLink.Error.NotOwner -> {
                uiState.loadError = BusinessRes.strings.business_link_error_not_owner.desc()
            }

            else -> {
                uiState.loadError = BusinessRes.strings.business_link_load_error.desc()
            }
        }
    }

    private fun onNameChanged(text: String) {
        uiState.name.clearError()
        showName(text.filterNot { it.isWhitespace() })
    }

    private fun saveLink() {
        val name = uiState.name.text
        launch(
            key = SAVE_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { uiState.save.startLoading() },
            call = { setBusinessLink(businessId, name) },
            onComplete = { onLinkSaved(it) },
            onError = { handleSaveError(it) },
            onTerminate = {
                uiState.save.stopLoading(enable = BusinessLink.isValidName(uiState.name.text))
            },
        )
    }

    private fun onLinkSaved(link: BusinessLink) {
        showName(link.name)
        uiState.name.clearError()
        uiState.notifications.add(PresentationNotification.SuccessHaptic)
        uiState.notifications.add(
            PresentationNotification.GlobalMessage(
                text = BusinessRes.strings.business_link_saved.desc(),
                state = PresentationNotification.GlobalMessage.State.SUCCESS,
            ),
        )
    }

    private fun handleSaveError(error: Throwable) {
        when (error) {
            is SetBusinessLink.Error.Invalid -> {
                uiState.name.showError(BusinessRes.strings.business_link_error_invalid.desc())
            }

            is SetBusinessLink.Error.Reserved -> {
                uiState.name.showError(BusinessRes.strings.business_link_error_reserved.desc())
            }

            is SetBusinessLink.Error.Taken -> {
                uiState.name.showError(BusinessRes.strings.business_link_error_taken.desc())
            }

            is SetBusinessLink.Error.ChangeLimitReached -> {
                showMessage(BusinessRes.strings.business_link_error_change_limit.desc())
            }

            is SetBusinessLink.Error.NotOwner -> {
                showMessage(BusinessRes.strings.business_link_error_not_owner.desc())
            }

            else -> {
                uiState.notifications.add(error.notification())
            }
        }
    }

    private fun showMessage(message: StringDesc) {
        uiState.notifications.add(PresentationNotification.Message.simple(message))
    }

    private fun renderLink(link: BusinessLink?) {
        val name = link?.name.orEmpty()
        if (uiState.name.text == savedName) {
            showName(name)
        }
        savedName = name
        uiState.share.isVisible = link != null
    }

    private fun showName(name: String) {
        uiState.name.text = name
        uiState.preview = previewOf(name)
        uiState.save.isEnabled = !uiState.save.isLoading && BusinessLink.isValidName(name)
    }

    private fun shareLink() {
        device.share("${siteUrl.value}/$savedName")
    }

    private fun previewOf(name: String): StringDesc {
        val host = siteUrl.value.substringAfter("://")
        return "$host/${BusinessLink.normalizeName(name)}".desc()
    }

    private companion object {
        const val REFRESH_KEY = "businessLinkRefresh"
        const val SAVE_KEY = "businessLinkSave"
    }
}
