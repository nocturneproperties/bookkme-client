package me.bookk.core.presentation.launcher

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.bookk.core.presentation.file.copyToFile
import me.bookk.core.presentation.file.createTempJpgFile
import java.io.File

@Composable
fun GalleryLauncher(
    launchGallery: Boolean,
    onResult: (List<File>) -> Unit,
    onGalleryLaunched: (() -> Unit)? = null,
) {
    val context = LocalContext.current as Activity
    val scope = rememberCoroutineScope()

    fun onResult(uris: List<Uri>) {
        scope.launch {
            val files = withContext(Dispatchers.IO) {
                uris.map { uri ->
                    context.cacheDir.createTempJpgFile().also { file ->
                        uri.copyToFile(context, file)
                    }
                }
            }
            onResult(files)
        }
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = ::onResult
    )

    LaunchedEffect(launchGallery) {
        if (launchGallery) {
            launcher.launch(IMAGE_MIME_TYPE)
            onGalleryLaunched?.invoke()
        }
    }
}

private const val IMAGE_MIME_TYPE = "image/*"