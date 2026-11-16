package me.bookk.core.presentation.date

class JsDateLocalizer : DateLocalizer {

    override fun forStyle(dateStyle: DateStyle): DateLocalizer.Formatter {
        return JsDateFormatter(dateStyle)
    }

    override fun strict(pattern: String, respectUserSettings: Boolean): DateLocalizer.Formatter {
        return when (respectUserSettings) {
            true -> JsSkeletonDateFormatter(IntlPattern.Skeleton(pattern))
            false -> JsStrictDateFormatter(pattern)
        }
    }
}
