package me.bookk.site.pages.terms

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import me.bookk.site.components.legal.LegalDocumentPage
import me.bookk.site.components.legal.addLegalPageData
import me.bookk.site.legal.LegalDocument

@InitRoute
fun initTermsVersionLanguagePage(ctx: InitRouteContext) {
    ctx.addLegalPageData(LegalDocument.TERMS_OF_SERVICE)
}

@Page("{version}/{language}")
@Layout(".components.layouts.PageLayout")
@Composable
fun TermsVersionLanguagePage() {
    LegalDocumentPage(LegalDocument.TERMS_OF_SERVICE)
}
