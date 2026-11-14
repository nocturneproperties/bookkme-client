package me.bookk.feature.authorization.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.bookk.feature.authorization.domain.api.ObserveSignUpRejection
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSessionDataSource
import me.bookk.feature.authorization.domain.entity.SignUpRejection

internal class ObserveSignUpRejectionImpl(
    private val signUpSessionDataSource: SignUpSessionDataSource,
) : ObserveSignUpRejection {

    override fun invoke(): Flow<SignUpRejection?> {
        return signUpSessionDataSource.observe()
            .map { it?.rejection }
            .distinctUntilChanged()
    }
}
