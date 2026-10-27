package build_src.convention

import build_src.constants.ProductFlavour
import com.android.build.api.dsl.ApplicationExtension
import java.io.File

fun ApplicationExtension.applyFlavourConvention(projectDir: File) {
    flavorDimensions.add("type")

    productFlavors {
        ProductFlavour.entries.forEach {
            create(it.title) {
                dimension = "type"
                applicationIdSuffix = ".${it.title}"
            }
        }
    }
}