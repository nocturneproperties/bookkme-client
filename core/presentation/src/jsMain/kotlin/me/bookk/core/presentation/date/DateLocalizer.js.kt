package me.bookk.core.presentation.date

actual enum class DateStyle(
    internal val date: IntlPattern,
    internal val dateTime: IntlPattern,
    internal val sameYearDateTime: IntlPattern,
    internal val sameYearDate: IntlPattern,
    internal val time: IntlPattern,
) {
    SHORT(
        date = IntlPattern.Styles(dateStyle = "short"),
        dateTime = IntlPattern.Styles(dateStyle = "short", timeStyle = "short"),
        sameYearDateTime = IntlPattern.Styles(dateStyle = "short", timeStyle = "short"),
        sameYearDate = IntlPattern.Styles(dateStyle = "short"),
        time = IntlPattern.Styles(timeStyle = "short"),
    ),
    MEDIUM(
        date = IntlPattern.Styles(dateStyle = "medium"),
        dateTime = IntlPattern.Styles(dateStyle = "medium", timeStyle = "short"),
        sameYearDateTime = IntlPattern.Styles(dateStyle = "medium", timeStyle = "short"),
        sameYearDate = IntlPattern.Styles(dateStyle = "medium"),
        time = IntlPattern.Styles(timeStyle = "medium"),
    ),
    LONG(
        date = IntlPattern.Styles(dateStyle = "long"),
        dateTime = IntlPattern.Styles(dateStyle = "long", timeStyle = "long"),
        sameYearDateTime = IntlPattern.Styles(dateStyle = "long", timeStyle = "long"),
        sameYearDate = IntlPattern.Styles(dateStyle = "long"),
        time = IntlPattern.Styles(timeStyle = "long"),
    ),
    D_MMM_YYYY_RELATIVE(
        date = IntlPattern.Skeleton("d MMM yyyy"),
        dateTime = IntlPattern.Skeleton("d MMM yyyy HH:mm"),
        sameYearDateTime = IntlPattern.Skeleton("d MMM HH:mm"),
        sameYearDate = IntlPattern.Skeleton("d MMM"),
        time = IntlPattern.Skeleton("HH:mm"),
    ),
    NARROW_WEEKDAY(
        date = IntlPattern.Skeleton("EEEEE"),
        dateTime = IntlPattern.Skeleton("EEEEE HH:mm"),
        sameYearDateTime = IntlPattern.Skeleton("EEEEE HH:mm"),
        sameYearDate = IntlPattern.Skeleton("EEEEE"),
        time = IntlPattern.Skeleton("HH:mm"),
    ),
    FULL_WEEKDAY(
        date = IntlPattern.Skeleton("EEEE"),
        dateTime = IntlPattern.Skeleton("EEEE HH:mm"),
        sameYearDateTime = IntlPattern.Skeleton("EEEE HH:mm"),
        sameYearDate = IntlPattern.Skeleton("EEEE"),
        time = IntlPattern.Skeleton("HH:mm"),
    ),
}
