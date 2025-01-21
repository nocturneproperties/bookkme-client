package me.bookk.feature.authorization.domain.datasource.authorization

class SignInData(
    val requestId: String,
    val deviceInfo: DeviceInfo,
    val publicKeyCredentialJson: String
) {

    class DeviceInfo(
        val deviceUUID: String,
        val deviceName: String,
    )
}