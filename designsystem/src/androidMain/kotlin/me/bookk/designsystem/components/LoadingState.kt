package me.bookk.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.bookk.designsystem.theme.color.LocalColors

@Composable
fun LoadingState(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(
            color = LocalColors.current.PrimaryText,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}