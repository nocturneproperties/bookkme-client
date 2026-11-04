package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.settings.domain.datasource.SettingsErrorCodes
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.settings.domain.api.EditProfile

internal class EditProfileImpl(
    private val profileCRUD: UserProfileCRUD,
) : EditProfile {
    override suspend fun invoke(firstName: String, lastName: String, email: String) {
        runCatching {
            profileCRUD.update(
                profileCRUD.get().copy(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    email = email.trim()
                )
            )
        }.onBusinessError {
            when (it.errorCode) {
                SettingsErrorCodes.USER_CONTACT_TAKEN -> throw EditProfile.Error.ContactAlreadyTaken()
            }
        }.getOrThrow()
    }
}