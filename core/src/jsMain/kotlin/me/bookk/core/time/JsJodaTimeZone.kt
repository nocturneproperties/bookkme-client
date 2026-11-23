package me.bookk.core.time

@JsModule("@js-joda/timezone/dist/js-joda-timezone-60-year-range")
@JsNonModule
internal external object JsJodaTimeZoneModule

@OptIn(ExperimentalJsExport::class)
@JsExport
val jsJodaTz: dynamic = JsJodaTimeZoneModule
