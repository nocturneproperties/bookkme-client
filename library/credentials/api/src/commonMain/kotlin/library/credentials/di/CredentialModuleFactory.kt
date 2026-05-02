package library.credentials.di

import library.credentials.api.CredentialManager

interface CredentialModuleFactory {
    fun createCredentialManager(): CredentialManager
}