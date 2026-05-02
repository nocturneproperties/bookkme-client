package me.bookk.feature.business.presentation.create

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.create.state.CreateBusinessState

class CreateBusinessViewModel(
    private val createBusiness: CreateBusiness,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createBusinessState(createInitData())

    fun onBusinessNameChanged(name: String) {
        uiState.name.text = name
        uiState.createBtn.isEnabled = name.length >= 2
    }

    fun onCreateClick() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.createBtn.isEnabled = false
                uiState.createBtn.isLoading = true
            },
            call = { createBusiness(uiState.name.text) },
            onComplete = {
                /**
                 * @see me.bookk.feature.business.presentation.bootstrap.BusinessBootstrapViewModel
                 * After successful business creation bootstrap will change root destination resulting
                 * in screen stack change
                 * */
            },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) },
            onTerminate = {
                uiState.createBtn.isEnabled = true
                uiState.createBtn.isLoading = false
            }
        )
    }

    companion object {
        fun createInitData(): CreateBusinessState.InitData {
            return CreateBusinessState.InitData(
                title = BusinessRes.strings.create_business_title.desc(),
                hint = BusinessRes.strings.create_business_name_hint.desc(),
                supportingText = BusinessRes.strings.create_business_name_supporting.desc(),
                buttonText = DesignSystem.strings.action_create.desc(),
                maxNameLength = 512
            )
        }
    }
}