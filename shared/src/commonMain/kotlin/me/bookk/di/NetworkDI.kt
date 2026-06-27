package me.bookk.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.header
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.IOException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import library.device.api.DeviceFacade
import me.bookk.core.Logger
import me.bookk.core.data.HttpClientType
import me.bookk.data.mock.MockedBackend
import me.bookk.feature.authorization.domain.api.GetTokenInfo
import me.bookk.feature.authorization.domain.api.RefreshToken
import me.bookk.shared.BuildKonfig
import me.bookk.shared.LoggerImpl
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal fun networkModule() = module {
    single { buildClient(installAuth = true) }
    factory(named(HttpClientType.NO_AUTH)) { buildClient(installAuth = false) }
}

private val refreshMutex = Mutex()
private val refreshLogger by lazy { Logger.create("Network") }

@OptIn(ExperimentalSerializationApi::class, ExperimentalUuidApi::class)
private fun Scope.buildClient(installAuth: Boolean): HttpClient {
    val config: HttpClientConfig<*>.() -> Unit = {
        expectSuccess = true
        install(Logging) {
            logger = LoggerImpl("KtorClient")
            level = if (BuildKonfig.DEBUG) {
                LogLevel.ALL
            } else {
                LogLevel.NONE
            }
        }
        install(ContentNegotiation) {
            protobuf(ProtoBuf { encodeDefaults = true })
        }
        install(HttpCache)
        install(Resources)
        install(HttpTimeout) {
            connectTimeoutMillis = 5000
            requestTimeoutMillis = 20000
            socketTimeoutMillis = 20000
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 3)
            retryOnExceptionIf { _, cause ->
                cause is IOException
            }
            exponentialDelay(
                baseDelayMs = 500,
                maxDelayMs = 5000
            )
        }
        install(UserAgent) {
            agent = buildString {
                append("BookkMe/${BuildKonfig.VERSION_NAME}")
            }
        }
        if (installAuth) {
            install(Auth) {
                bearer {
                    loadTokens {
                        get<GetTokenInfo>().invoke()?.let {
                            BearerTokens(it.accessToken, null)
                        }
                    }
                    refreshTokens {
                        val tokenAccessor = get<GetTokenInfo>()
                        val currentToken = tokenAccessor() ?: return@refreshTokens null
                        refreshLogger.d("Start refreshing from: ${response.request.url}, token: $currentToken")
                        refreshMutex.withLock {
                            refreshLogger.d("Entered lock from: ${response.request.url}")
                            val tokensAfterLock = tokenAccessor()
                            if (currentToken != tokensAfterLock && tokensAfterLock != null) {
                                refreshLogger.d("Tokens already refreshed: ${response.request.url}, token: $tokensAfterLock")
                                BearerTokens(tokensAfterLock.accessToken, tokensAfterLock.refreshToken)
                            } else {
                                runCatching {
                                    val refresh = get<RefreshToken>().invoke(currentToken.refreshToken)
                                    refreshLogger.d("Token refresh success")
                                    BearerTokens(refresh.accessToken, refresh.refreshToken)
                                }.onFailure {
                                    refreshLogger.d("Token refresh failed, null is set")
                                }.getOrNull()
                            }
                        }
                    }
                }
            }
        }

        defaultRequest {
            if (BuildKonfig.DEBUG) {
                header("X-Debug", true)
            }
            header("X-App-Version", BuildKonfig.VERSION_NAME)
            header("X-Platform", get<DeviceFacade>().getPlatformName())
            if (installAuth) {
                headers["Idempotency-Key"] = Uuid.random().toHexString()
            }
            url(BuildKonfig.BASE_URL)
            contentType(ContentType.Application.ProtoBuf)
        }
    }
    return if (BuildKonfig.VARIANT.startsWith("mock")) {
        HttpClient(MockedBackend.engine, config)
    } else {
        HttpClient(config)
    }
}