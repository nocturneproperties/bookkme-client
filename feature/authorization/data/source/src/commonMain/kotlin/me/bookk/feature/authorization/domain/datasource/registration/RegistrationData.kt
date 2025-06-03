package me.bookk.feature.authorization.domain.datasource.registration

class RegistrationData(
    val requestId: String,
    val deviceInfo: DeviceInfo,
    val userInfo: UserInfo,
    val publicKeyCredentialJson: String
) {

    class UserInfo(
        val name: String,
        val lastName: String,
        val email: String
    )

    class DeviceInfo(
        val deviceUUID: String,
        val deviceName: String,
    )
}