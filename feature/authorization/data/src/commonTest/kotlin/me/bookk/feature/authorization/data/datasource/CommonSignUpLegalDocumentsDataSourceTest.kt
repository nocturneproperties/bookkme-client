package me.bookk.feature.authorization.data.datasource

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument.Document
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocuments
import kotlin.reflect.KClass
import kotlin.reflect.safeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class CommonSignUpLegalDocumentsDataSourceTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakePreferences : Preferences {
        private val values = mutableMapOf<String, Any>()

        override suspend fun <T : Any> get(key: Preferences.Key<T>, cls: KClass<T>): T? {
            return cls.safeCast(values[key.name])
        }

        override fun <T : Any> getFlow(key: Preferences.Key<T>, cls: KClass<T>): Flow<T?> {
            return flowOf(cls.safeCast(values[key.name]))
        }

        override suspend fun <T : Any> set(key: Preferences.Key<T>, value: T?, cls: KClass<T>) {
            if (value == null) values.remove(key.name) else values[key.name] = value
        }

        override suspend fun <T : Any> remove(key: Preferences.Key<T>, cls: KClass<T>) {
            values.remove(key.name)
        }

        override suspend fun clear() {
            values.clear()
        }
    }

    private class Fixture {
        val preferences = FakePreferences()
        val provider = mock<PreferenceProvider> {
            every { get("signup_legal_documents_prefs") } returns preferences
        }
        val documents = SignUpLegalDocuments(
            SignUpLegalDocument(
                Document.PRIVACY_POLICY,
                "privacy-v3",
                "https://example.com/privacy",
            ),
            SignUpLegalDocument(
                Document.TERMS_OF_SERVICE,
                "terms-v2",
                "https://example.com/terms?a=1&b=2",
            ),
        )
        val sut = CommonSignUpLegalDocumentsDataSource(provider)
    }

    @Test
    fun `returns no documents before preload`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut.getDocuments()

        then()
        assertNull(result)
    }

    @Test
    fun `restores both document versions and links from preferences after recreation`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.sut.saveDocuments(fixture.documents)
            val recreated = CommonSignUpLegalDocumentsDataSource(fixture.provider)

            whenn()
            val result = recreated.getDocuments()

            then()
            assertEquals(fixture.documents, result)
        }

    @Test
    fun `replaces both documents together`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.saveDocuments(fixture.documents)
        val updated = fixture.documents.copy(
            privacyPolicy = fixture.documents.privacyPolicy.copy(version = "privacy-v4"),
            termsOfService = fixture.documents.termsOfService.copy(
                url = "https://example.com/new-terms",
            ),
        )

        whenn()
        fixture.sut.saveDocuments(updated)

        then()
        assertEquals(updated, fixture.sut.getDocuments())
    }
}
