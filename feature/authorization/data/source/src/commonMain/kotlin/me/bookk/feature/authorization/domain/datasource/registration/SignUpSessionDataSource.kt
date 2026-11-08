package me.bookk.feature.authorization.domain.datasource.registration

interface SignUpSessionDataSource {
    fun get(): SignUpSession?
    fun save(session: SignUpSession)
    fun clear()
}
