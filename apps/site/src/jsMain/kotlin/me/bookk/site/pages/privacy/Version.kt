package me.bookk.site.pages.privacy

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import me.bookk.site.legal.LegalDocument
import me.bookk.site.legal.ui.LegalDocumentPage
import me.bookk.site.legal.ui.addLegalPageData

@InitRoute
fun initPrivacyVersionPage(ctx: InitRouteContext) {
    ctx.addLegalPageData(LegalDocument.PRIVACY_POLICY)
}

@Page("{version}")
@Layout(".layout.PageLayout")
@Composable
fun PrivacyVersionPage() {
    LegalDocumentPage(LegalDocument.PRIVACY_POLICY)
}
