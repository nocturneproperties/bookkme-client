package me.bookk.feature.settings.domain.impl

import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.settings.domain.api.EditProfile

internal class EditProfileImpl(
    private val profileCRUD: UserProfileCRUD,
) : EditProfile {
    override suspend fun invoke(firstName: String, lastName: String, email: String) {
        profileCRUD.update(
            profileCRUD.get().copy(
                firstName = firstName,
                lastName = lastName,
                email = email
            )
        )
    }
}