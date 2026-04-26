package build_src.constants

import org.gradle.internal.extensions.stdlib.capitalized

enum class ProductFlavour(val title: String) {
    DEV("dev"),
    STAGE("stage"),
    PROD("prod");

    companion object {
        fun from(variant: String): ProductFlavour {
            return entries.firstOrNull { variant.contains(it.title.capitalized()) } ?: PROD
        }
    }
}