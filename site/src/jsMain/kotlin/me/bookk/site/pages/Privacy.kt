package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import me.bookk.site.components.legal.LegalDocumentPage
import me.bookk.site.components.legal.addLegalPageData
import me.bookk.site.legal.LegalDocument

@InitRoute
fun initPrivacyPage(ctx: InitRouteContext) {
    ctx.addLegalPageData(LegalDocument.PRIVACY_POLICY)
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun PrivacyPage() {
    LegalDocumentPage(LegalDocument.PRIVACY_POLICY)
}
