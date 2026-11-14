package me.bookk.feature.authorization.domain.datasource.registration

import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import kotlin.uuid.Uuid

class RegistrationData(
    val requestId: String,
    val deviceInfo: DeviceInfo,
    val userInfo: UserInfo,
    val publicKeyCredentialJson: String,
    val acceptedDocuments: AcceptedLegalDocuments,
    val verificationId: Uuid,
    val emailCode: String,
) {

    class UserInfo(
        val name: String,
        val lastName: String,
        val email: String,
    )

    class DeviceInfo(
        val deviceUUID: String,
        val deviceName: String,
    )
}
