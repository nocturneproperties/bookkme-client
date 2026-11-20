package me.bookk.site.pages.terms

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import me.bookk.site.legal.LegalDocument
import me.bookk.site.legal.ui.LegalDocumentPage
import me.bookk.site.legal.ui.addLegalPageData

@InitRoute
fun initTermsVersionPage(ctx: InitRouteContext) {
    ctx.addLegalPageData(LegalDocument.TERMS_OF_SERVICE)
}

@Page("{version}")
@Layout(".layout.PageLayout")
@Composable
fun TermsVersionPage() {
    LegalDocumentPage(LegalDocument.TERMS_OF_SERVICE)
}
