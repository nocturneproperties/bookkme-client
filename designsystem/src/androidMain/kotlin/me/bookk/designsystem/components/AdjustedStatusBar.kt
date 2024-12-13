package me.bookk.designsystem.components

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat

fun isColorDark(color: Int): Boolean {
    return ColorUtils.calculateLuminance(color) < 0.5
}

fun getStatusBarColor(view: View): Int {
    val bitmap = Bitmap.createBitmap(view.width, 1, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap.getPixel(0, 0)
}

@Composable
fun DynamicStatusBar() {
    val view = LocalView.current
    var statusBarColor by remember { mutableIntStateOf(Color.TRANSPARENT) }
    var isDarkIcons by remember { mutableStateOf(false) }

    DisposableEffect(view) {
        val window = (view.context as Activity).window
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        val viewTreeObserver = view.viewTreeObserver
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            statusBarColor = getStatusBarColor(view)
            isDarkIcons = !isColorDark(statusBarColor)
            insetsController.isAppearanceLightStatusBars = isDarkIcons
        }

        viewTreeObserver.addOnGlobalLayoutListener(listener)

        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
            insetsController.isAppearanceLightStatusBars = true // Default state
        }
    }
}