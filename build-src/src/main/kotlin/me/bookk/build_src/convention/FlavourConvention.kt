package me.bookk.build_src.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension

fun CommonExtension<*, *, *, *, *, *>.applyFlavourConvention() {
    flavorDimensions.add("type")

    productFlavors {
        create("dev") {
            dimension = "type"
            matchingFallbacks
        }

        create("stage") {
            dimension = "type"
        }

        create("prod") {
            dimension = "type"
        }
    }
}

fun ApplicationExtension.applyFlavourConvention() {
    flavorDimensions.add("type")

    productFlavors {
        create("dev") {
            dimension = "type"
            applicationIdSuffix = ".dev"
        }

        create("stage") {
            dimension = "type"
            applicationIdSuffix = ".stage"
        }

        create("prod") {
            dimension = "type"
        }
    }
}