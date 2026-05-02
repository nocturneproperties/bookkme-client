package library.biometry.api

import platform.LocalAuthentication.LAContext

object BiometryContext {
    val current = LAContext().apply {
        touchIDAuthenticationAllowableReuseDuration = 10.0
    }
}