package library.biometry.impl


import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.suspendCancellableCoroutine
import library.biometry.api.Biometry
import library.biometry.api.BiometryOptManager
import me.bookk.core.android.AndroidActivityAware
import kotlin.coroutines.resume

internal class AndroidBiometry(
    optManager: BiometryOptManager
) : AndroidActivityAware(), Biometry, BiometryOptManager by optManager {

    override fun isBiometryAvailable(): Boolean {
        val biometricManager = BiometricManager.from(requireActivity())
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false

            else -> false
        }
    }

    override suspend fun request(title: StringDesc, reason: StringDesc, cancelText: StringDesc) {
        val context = requireActivity()
        val executor = ContextCompat.getMainExecutor(context)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title.toString(context))
            .setSubtitle(reason.toString(context))
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setNegativeButtonText(cancelText.toString(context))
            .build()
        suspendCancellableCoroutine { continuation ->
            val prompt = BiometricPrompt(
                requireActivity() as FragmentActivity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        continuation.resume(result)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        if (errorCode == CODE_CANCELLED_BY_USER) {
                            continuation.cancel(Biometry.Error.UserCancelled())
                        } else {
                            continuation.cancel()
                        }
                    }

                    override fun onAuthenticationFailed() {
                        //This is not terminal operation, just shows that one of attempts was failed
                    }
                }
            )
            prompt.authenticate(promptInfo)
        }
    }

    companion object {
        private const val CODE_CANCELLED_BY_USER = 10
    }
}