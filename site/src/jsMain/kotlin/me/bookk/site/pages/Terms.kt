package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import me.bookk.site.legal.LegalDocument
import me.bookk.site.legal.ui.LegalDocumentPage
import me.bookk.site.legal.ui.addLegalPageData

@InitRoute
fun initTermsPage(ctx: InitRouteContext) {
    ctx.addLegalPageData(LegalDocument.TERMS_OF_SERVICE)
}

@Page
@Layout(".layout.PageLayout")
@Composable
fun TermsPage() {
    LegalDocumentPage(LegalDocument.TERMS_OF_SERVICE)
}
