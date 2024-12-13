package me.bookk.designsystem.theme.text

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
object AppTypography {
    val largeTitle = TextStyle(
        fontSize = 34.sp,
        lineHeight = 41.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )

    val title1Bold = TextStyle(
        fontSize = 28.sp,
        lineHeight = 34.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )

    val title2Bold = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )

    val title3Bold = TextStyle(
        fontSize = 20.sp,
        lineHeight = 25.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )

    val title3SemiBold = TextStyle(
        fontSize = 20.sp,
        lineHeight = 25.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Normal
    )

    val title3BoldItalic = TextStyle(
        fontSize = 20.sp,
        lineHeight = 25.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    )

    val body1SemiBold = TextStyle(
        fontSize = 17.sp,
        lineHeight = 22.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Normal
    )

    val body1Regular = TextStyle(
        fontSize = 17.sp,
        lineHeight = 22.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    )

    val body2SemiBold = TextStyle(
        fontSize = 15.sp,
        lineHeight = 20.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Normal
    )

    val body2Regular = TextStyle(
        fontSize = 15.sp,
        lineHeight = 20.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    )

    val caption1Regular = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    )

    val caption1SemiBold = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Normal
    )

    val caption2Bold = TextStyle(
        fontSize = 11.sp,
        lineHeight = 13.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )

    val caption2SemiBold = TextStyle(
        fontSize = 11.sp,
        lineHeight = 13.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Normal
    )

    val caption3Regular = TextStyle(
        fontSize = 10.sp,
        lineHeight = 12.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    )
}

val LocalAppTypography = staticCompositionLocalOf { AppTypography }