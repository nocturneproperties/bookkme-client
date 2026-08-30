package me.bookk.feature.employees.presentation.screen.invite

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.employees.resources.EmployeesRes
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitationStatus
import kotlin.uuid.Uuid

data class InvitationItem(
    val id: Uuid,
    val initials: String,
    val email: String,
    val sentOn: StringDesc,
    val status: UIInvitationStatus,
    val onLongPress: (() -> Unit)? = null
)

class UIInvitationStatus(
    val label: StringDesc,
    val color: ColorToken
) {
    internal constructor(status: EmployeeInvitationStatus) : this(
        label = status.label(),
        color = status.color()
    )
}

private fun EmployeeInvitationStatus.label(): StringDesc = when (this) {
    EmployeeInvitationStatus.PENDING -> EmployeesRes.strings.employees_invite_status_pending.desc()
    EmployeeInvitationStatus.APPROVED -> EmployeesRes.strings.employees_invite_status_accepted.desc()
    EmployeeInvitationStatus.REJECTED -> EmployeesRes.strings.employees_invite_status_rejected.desc()
    EmployeeInvitationStatus.EXPIRED -> EmployeesRes.strings.employees_invite_status_expired.desc()
    EmployeeInvitationStatus.REVOKED -> EmployeesRes.strings.employees_invite_status_revoked.desc()
}

private fun EmployeeInvitationStatus.color(): ColorToken = when (this) {
    EmployeeInvitationStatus.PENDING -> ColorToken.ActionText
    EmployeeInvitationStatus.APPROVED -> ColorToken.Success
    EmployeeInvitationStatus.REJECTED -> ColorToken.Error
    EmployeeInvitationStatus.EXPIRED -> ColorToken.Inactive
    EmployeeInvitationStatus.REVOKED -> ColorToken.Inactive
}
