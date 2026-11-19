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
fun initDpaPage(ctx: InitRouteContext) {
    ctx.addLegalPageData(LegalDocument.DATA_PROCESSING_AGREEMENT)
}

@Page
@Layout(".layout.PageLayout")
@Composable
fun DpaPage() {
    LegalDocumentPage(LegalDocument.DATA_PROCESSING_AGREEMENT)
}
