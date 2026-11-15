package me.bookk.core.time

@JsModule("@js-joda/timezone")
@JsNonModule
internal external object JsJodaTimeZoneModule

@OptIn(ExperimentalJsExport::class)
@JsExport
val jsJodaTz: dynamic = JsJodaTimeZoneModule
