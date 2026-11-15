package me.bookk.site.pages.dpa

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import me.bookk.site.components.legal.LegalDocumentPage
import me.bookk.site.components.legal.addLegalPageData
import me.bookk.site.legal.LegalDocument

@InitRoute
fun initDpaVersionPage(ctx: InitRouteContext) {
    ctx.addLegalPageData(LegalDocument.DATA_PROCESSING_AGREEMENT)
}

@Page("{version}")
@Layout(".components.layouts.PageLayout")
@Composable
fun DpaVersionPage() {
    LegalDocumentPage(LegalDocument.DATA_PROCESSING_AGREEMENT)
}
