package me.bookk.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import me.bookk.core.data.HttpClientType
import me.bookk.feature.authorization.domain.api.GetTokenInfo
import me.bookk.feature.authorization.domain.api.RefreshToken
import me.bookk.shared.BuildKonfig
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import io.ktor.client.plugins.logging.Logger as KtorLogger

internal fun networkModule() = module {
    single { buildClient(installAuth = true) }
    factory(named(HttpClientType.NO_AUTH)) { buildClient(installAuth = false) }
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalUuidApi::class)
private fun Scope.buildClient(installAuth: Boolean): HttpClient {
    return HttpClient {
        expectSuccess = true
        install(Logging) {
            logger = KtorLogger.DEFAULT
            level = if (BuildKonfig.DEBUG) {
                LogLevel.ALL
            } else {
                LogLevel.NONE
            }
        }
        install(ContentNegotiation) {
            protobuf(ProtoBuf { encodeDefaults = true })
        }
        install(Resources)
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
        if (installAuth) {
            install(Auth) {
                bearer {
                    loadTokens {
                        get<GetTokenInfo>().invoke()?.let {
                            BearerTokens(it.accessToken, it.refreshToken)
                        }
                    }
                    refreshTokens {
                        get<GetTokenInfo>().invoke()?.let {
                            runCatching {
                                val refresh = get<RefreshToken>().invoke(it.refreshToken)
                                BearerTokens(refresh.accessToken, refresh.refreshToken)
                            }.getOrNull()
                        }
                    }
                }
            }
        }

        defaultRequest {
            headers["Idempotency-Key"] = Uuid.random().toHexString()
            url(BuildKonfig.BASE_URL)
            contentType(ContentType.Application.ProtoBuf)
        }
    }
}