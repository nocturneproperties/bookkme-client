package me.bookk.site.config

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.site.designsystem.resources.AppLanguage
import me.bookk.site.routing.localizedPageRoutes
import kotlin.test.Test
import kotlin.test.assertEquals

class SiteBuildConfigTest {

    @Test
    fun `exports and maps every localized static page`() = runUnitTest {
        given()
        val routes = localizedPageRoutes

        whenn()
        val staticRoutes = routes.filterNot { '{' in it }

        then()
        assertEquals(SiteBuildConfig.pagePaths, staticRoutes)
    }

    @Test
    fun `exports and maps every site language with the default first`() = runUnitTest {
        given()
        val languages = AppLanguage.entries

        whenn()
        val tags =
            listOf(AppLanguage.DEFAULT.tag) + (languages - AppLanguage.DEFAULT).map { it.tag }

        then()
        assertEquals(SiteBuildConfig.languages, tags)
    }
}
