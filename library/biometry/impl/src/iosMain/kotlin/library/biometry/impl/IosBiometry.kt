package library.biometry.impl


import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.suspendCancellableCoroutine
import library.biometry.api.Biometry
import library.biometry.api.Biometry.Error
import library.biometry.api.BiometryContext
import library.biometry.api.BiometryOptManager
import platform.LocalAuthentication.LAErrorUserCancel
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resume

internal class IosBiometry(
    optManager: CommonBiometryOptManager
) : Biometry, BiometryOptManager by optManager {

    override fun isBiometryAvailable(): Boolean {
        return BiometryContext.current.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
    }

    override suspend fun request(title: StringDesc, reason: StringDesc, cancelText: StringDesc) {
        suspendCancellableCoroutine { continuation ->
            BiometryContext.current.evaluatePolicy(
                LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                reason.localized()
            ) { success, error ->
                if (success) {
                    continuation.resume(success)
                } else if (error?.code == LAErrorUserCancel) {
                    continuation.cancel(Error.UserCancelled())
                } else {
                    continuation.cancel(Error.AuthenticationFailed(error?.localizedFailureReason.orEmpty()))
                }
            }
        }
    }
}