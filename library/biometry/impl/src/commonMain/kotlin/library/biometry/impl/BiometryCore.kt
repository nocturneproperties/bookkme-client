package library.biometry.impl

import eu.advapay.mobilebank.core.domain.logout.LogOutAction
import library.biometry.api.BiometryOptManager
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set

class CommonBiometryOptManager(
    preferenceProvider: PreferenceProvider
) : BiometryOptManager, LogOutAction {

    private val preferences = preferenceProvider.get("biometry_prefs")

    override suspend fun optIn() {
        preferences.set(Key.optIn, true)
    }

    override suspend fun optOut() {
        preferences.set(Key.optIn, false)
    }

    override suspend fun isOptedIn(): Boolean? {
        return preferences.get(Key.optIn)
    }

    override suspend fun doOnLogOut() {
        preferences.set(Key.optIn, null)
    }

    private object Key {
        val optIn = Preferences.Key<Boolean>("biometry_opt_in")
    }
}