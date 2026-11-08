package me.bookk.feature.authorization.domain.datasource.registration

import kotlinx.coroutines.flow.Flow

interface SignUpSessionDataSource {
    fun get(): SignUpSession?
    fun observe(): Flow<SignUpSession?>
    fun save(session: SignUpSession)
    fun clear()
}
