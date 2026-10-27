package me.bookk.feature.employees.presentation.screen.invite

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.employees.resources.EmployeesRes
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitationStatus
import kotlin.uuid.Uuid

val MaskedInvitationCode: StringDesc = "••••••••".desc()

data class InvitationItem(
    val id: Uuid,
    val code: StringDesc,
    val createdOn: StringDesc,
    val status: UIInvitationStatus,
    val onClick: (() -> Unit)? = null
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
    EmployeeInvitationStatus.REDEEMED -> EmployeesRes.strings.employees_invite_status_redeemed.desc()
    EmployeeInvitationStatus.EXPIRED -> EmployeesRes.strings.employees_invite_status_expired.desc()
    EmployeeInvitationStatus.REVOKED -> EmployeesRes.strings.employees_invite_status_revoked.desc()
}

private fun EmployeeInvitationStatus.color(): ColorToken = when (this) {
    EmployeeInvitationStatus.PENDING -> ColorToken.ActionText
    EmployeeInvitationStatus.REDEEMED -> ColorToken.Success
    EmployeeInvitationStatus.EXPIRED -> ColorToken.Inactive
    EmployeeInvitationStatus.REVOKED -> ColorToken.Inactive
}
