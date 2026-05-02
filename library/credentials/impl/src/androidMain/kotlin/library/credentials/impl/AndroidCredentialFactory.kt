package library.credentials.impl

import library.credentials.api.CredentialManager
import library.credentials.di.CredentialModuleFactory

class AndroidCredentialFactory : CredentialModuleFactory {
    override fun createCredentialManager(): CredentialManager {
        return AndroidCredentialManager()
    }
}