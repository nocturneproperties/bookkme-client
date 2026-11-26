package me.bookk.feature.settings.domain.impl

import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.entity.ActionConfirmation
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.settings.domain.api.DeleteAccount
import me.bookk.feature.settings.domain.api.entity.ActionOutcome
import kotlin.uuid.Uuid

internal class DeleteAccountImpl(
    authorizationDataSource: AuthorizationDataSource,
    private val confirmation: ConfirmSensitiveAction,
    private val profile: UserProfileCRUD,
) : DeleteAccount {
    private val accountRemoval = AccountRemoval(authorizationDataSource)

    override suspend fun invoke(): ActionOutcome<Unit> {
        val confirmedAction = confirmAccountDeletion()
        accountRemoval.deleteOnRemote(confirmedAction.token)
        return ActionOutcome.Completed(Unit)
    }

    private suspend fun confirmAccountDeletion(): ActionConfirmation {
        val userId = profile.get().id
        return runCatching {
            confirmation(
                SensitiveAction.DELETE_ACCOUNT,
                Uuid.NIL,
                userId,
            )
        }
            .getOrElse {
                throw when (it) {
                    is ConfirmSensitiveAction.Error -> {
                        DeleteAccount.Error.AccountVerificationFailed()
                    }

                    else -> {
                        it
                    }
                }
            }
    }
}
