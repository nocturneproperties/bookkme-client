package me.bookk.site.components.legal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.rememberPageContext
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.components.layouts.PageLayoutData
import me.bookk.site.components.sections.NotFoundMessage
import me.bookk.site.designsystem.resources.WebLocalization
import me.bookk.site.legal.LegalDocument
import me.bookk.site.legal.LegalDocuments
import me.bookk.site.legal.LegalPageRequest
import me.bookk.site.legal.LegalPages
import kotlin.time.Clock

private const val VERSION_PARAM = "version"
private const val LANGUAGE_PARAM = "language"

fun InitRouteContext.addLegalPageData(document: LegalDocument) {
    data.add(PageLayoutData(document.title.desc()))
}

@Composable
fun LegalDocumentPage(document: LegalDocument) {
    val params = rememberPageContext().route.dynamicParams
    val version = params[VERSION_PARAM]
    val requestedLanguage = params[LANGUAGE_PARAM]
    val siteLanguage = WebLocalization.language
    val page = remember(document, version, requestedLanguage, siteLanguage) {
        LegalPages.build(
            LegalDocuments.history(document),
            LegalPageRequest(version, requestedLanguage),
            siteLanguage,
            Clock.System.now(),
        )
    }
    if (page == null) {
        NotFoundMessage()
    } else {
        LegalDocumentContent(page)
    }
}
