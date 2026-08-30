package me.bookk.feature.employees.presentation.screen.invite

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import library.validation.api.ValidateEmail
import me.bookk.android.feature.employees.resources.EmployeesRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.clearError
import me.bookk.designsystem.uistate.showError
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.employees.domain.api.CreateEmployeeInvitation
import me.bookk.feature.employees.domain.api.GetEmployeeInvitations
import me.bookk.feature.employees.domain.api.RevokeEmployeeInvitation
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitationStatus
import me.bookk.feature.employees.presentation.EmployeesStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class InviteEmployeeViewModel(
    @InjectedParam private val businessId: Uuid,
    private val createEmployeeInvitation: CreateEmployeeInvitation,
    private val getEmployeeInvitations: GetEmployeeInvitations,
    private val revokeEmployeeInvitation: RevokeEmployeeInvitation,
    private val validateEmail: ValidateEmail,
    private val dateLocalizer: DateLocalizer,
    stateFactory: EmployeesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: InviteEmployeeState = stateFactory.createInviteEmployeeState().setup()

    init {
        loadInvitations()
    }

    private fun loadInvitations() {
        launchCached(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.refreshState.isRefreshing = true },
            call = { getEmployeeInvitations.cached(businessId, it) },
            onComplete = {
                val items = it
                    .sortedByDescending(EmployeeInvitation::createdAt)
                    .map { invitation -> invitation.toItem() }
                uiState.invitationsList.replace(items)
            },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = {
                uiState.refreshState.isRefreshing = false
                uiState.invitationsList.isInitialLoading = false
            }
        )
    }

    private fun onEmailChanged(text: String) {
        uiState.emailField.text = text
        uiState.emailField.clearError()
        uiState.sendButton.isEnabled = text.isNotBlank()
    }

    private fun onSendInvite() {
        val email = uiState.emailField.text
        if (validateEmail(email) !is ValidateEmail.Result.Valid) {
            uiState.emailField.showError(EmployeesRes.strings.employees_invite_email_invalid.desc())
            return
        }
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.sendButton.startLoading() },
            call = { createEmployeeInvitation(businessId, email) },
            onComplete = {
                uiState.emailField.text = ""
                uiState.sendButton.isEnabled = false
                loadInvitations()
            },
            onError = {
                when (it) {
                    is CreateEmployeeInvitation.Error.InvitationExists ->
                        uiState.emailField.showError(EmployeesRes.strings.employees_invite_error_exists.desc())

                    is CreateEmployeeInvitation.Error.EmployeeExists ->
                        uiState.emailField.showError(EmployeesRes.strings.employees_invite_error_employee_exists.desc())

                    is CreateEmployeeInvitation.Error.ValidationError ->
                        uiState.emailField.showError(EmployeesRes.strings.employees_invite_email_invalid.desc())

                    else -> uiState.notifications.add(it.notification())
                }
            },
            onTerminate = { uiState.sendButton.stopLoading() }
        )
    }

    private fun onRevokeInvitationLongPress(id: Uuid, email: String) {
        uiState.notifications.add(
            PresentationNotification.Message(
                title = EmployeesRes.strings.employees_invite_revoke_dialog_title.desc(),
                message = EmployeesRes.strings.employees_invite_revoke_dialog_message.format(email),
                buttons = listOf(
                    ButtonDescriptor(text = DesignSystem.strings.action_cancel.desc()),
                    ButtonDescriptor(
                        text = EmployeesRes.strings.employees_invite_revoke_action.desc(),
                        actionType = ActionType.NEGATIVE,
                        onClick = { revokeInvitation(id) }
                    )
                )
            )
        )
    }

    private fun revokeInvitation(id: Uuid) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { revokeEmployeeInvitation(businessId, id) },
            onComplete = { loadInvitations() },
            onError = {
                when (it) {
                    is RevokeEmployeeInvitation.Error.AlreadyProcessed ->
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                EmployeesRes.strings.employees_invite_revoke_error_already_processed.desc()
                            )
                        )

                    else -> uiState.notifications.add(it.notification())
                }
            }
        )
    }

    private fun EmployeeInvitation.toItem(): InvitationItem {
        val formatter = dateLocalizer.forStyle(DateStyle.D_MMM_YYYY_RELATIVE)
        return InvitationItem(
            id = id,
            initials = email.substringBefore("@").take(2).uppercase(),
            email = email,
            sentOn = EmployeesRes.strings.employees_invite_sent_on.format(
                formatter.format(createdAt.date, relative = true)
            ),
            status = UIInvitationStatus(status),
            onLongPress = if (status == EmployeeInvitationStatus.PENDING) {
                weakVMClosure { it.onRevokeInvitationLongPress(id, email) }
            } else {
                null
            }
        )
    }

    private fun InviteEmployeeState.setup() = apply {
        appBar.title = EmployeesRes.strings.employees_invite_title.desc()
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(InviteEmployeeDestinations.Back) }

        descriptionText = EmployeesRes.strings.employees_invite_description.desc()

        emailField.placeholder = EmployeesRes.strings.employees_invite_email_placeholder.desc()
        emailField.inputType = InputType.EMAIL
        emailField.onTextChanged = weakVMClosure { vm, text -> vm.onEmailChanged(text) }

        sendButton.text = EmployeesRes.strings.employees_invite_send_button.desc()
        sendButton.isEnabled = false
        sendButton.onClick = weakVMClosure { it.onSendInvite() }

        invitationsHeader = EmployeesRes.strings.employees_invite_invitations_header.desc()
        refreshState.onRefresh = weakVMClosure { it.loadInvitations() }
        invitationsList.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = EmployeesRes.strings.employees_invite_invitations_empty.desc()
        )
    }
}
