package library.credentials.impl

import android.content.Context
import library.credentials.api.CredentialManager
import me.bookk.core.android.AndroidActivityAware
import androidx.credentials.CredentialManager.Companion as PlatformCredentialManager

internal class AndroidCredentialManager(
    context: Context
) : CredentialManager, AndroidActivityAware() {

    private val credentialManager = PlatformCredentialManager.create(context)

}