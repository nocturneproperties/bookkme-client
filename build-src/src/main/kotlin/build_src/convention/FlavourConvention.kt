package build_src.convention

import build_src.constants.ProductFlavour
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension

fun CommonExtension<*, *, *, *, *, *>.applyFlavourConvention() {
    flavorDimensions.add("type")

    productFlavors {
        create(ProductFlavour.DEV.title) {
            dimension = "type"
        }

        create(ProductFlavour.STAGE.title) {
            dimension = "type"
        }

        create(ProductFlavour.PROD.title) {
            dimension = "type"
        }
    }
}

fun ApplicationExtension.applyFlavourConvention() {
    flavorDimensions.add("type")

    productFlavors {
        create(ProductFlavour.DEV.title) {
            dimension = "type"
            applicationIdSuffix = ".${ProductFlavour.DEV.title}"
        }

        create(ProductFlavour.STAGE.title) {
            dimension = "type"
            applicationIdSuffix = ".${ProductFlavour.STAGE.title}"
        }

        create(ProductFlavour.PROD.title) {
            dimension = "type"
        }
    }
}