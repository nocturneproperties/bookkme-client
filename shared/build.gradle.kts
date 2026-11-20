import build_src.constants.ApplicationConfig

plugins {
    id(libs.plugins.convention.kmm.library.kotlin.get().pluginId)
    alias(libs.plugins.kmm.resources)
}

kotlin {
    android {
        namespace = "${ApplicationConfig.ROOT_PACKAGE}.shared"
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core
            implementation(projects.database.business)
            implementation(projects.core.data)
            implementation(projects.shared.common)
            api(projects.core)
            api(projects.core.domain)
            api(projects.core.presentation)
            api(projects.designsystem)
            // Library
            implementation(projects.library.device.api)
            implementation(projects.library.device.impl)
            implementation(projects.library.cache.impl)
            implementation(projects.library.permissions.impl)
            implementation(projects.library.files.impl)
            implementation(projects.library.validation.impl)
            api(projects.library.notifications.api)
            api(projects.library.crashreporting.api)
            implementation(projects.library.notifications.impl)
            api(projects.library.credentials.api)
            implementation(projects.library.credentials.impl)
            api(projects.library.money)
            api(projects.library.biometry.api)
            api(projects.library.picker)
            implementation(projects.library.biometry.impl)
            // Auth
            implementation(projects.feature.authorization.domain.api)
            implementation(projects.feature.authorization.domain.impl)
            implementation(projects.feature.authorization.data)
            api(projects.feature.authorization.presentation)
            // Dashboard
            api(projects.feature.dashboard.presentation)
            // Settings
            implementation(projects.feature.settings.domain.impl)
            implementation(projects.feature.settings.data)
            api(projects.feature.settings.presentation)
            // Business
            implementation(projects.feature.business.domain.impl)
            implementation(projects.feature.business.data)
            api(projects.feature.business.presentation)
            // Clients
            implementation(projects.feature.clients.domain.impl)
            implementation(projects.feature.clients.data)
            api(projects.feature.clients.presentation)
            // Clients
            implementation(projects.feature.services.domain.impl)
            implementation(projects.feature.services.data)
            api(projects.feature.services.presentation)
            // Employees
            implementation(projects.feature.employees.domain.impl)
            implementation(projects.feature.employees.data)
            api(projects.feature.employees.presentation)
            // Appointments
            implementation(projects.feature.appointments.domain.impl)
            implementation(projects.feature.appointments.data)
            api(projects.feature.appointments.presentation)
            // Libs
            api(libs.kotlinx.datetime)
            api(libs.kmm.resources)
        }
        commonTest.dependencies {
            implementation(projects.core.testFixtures)
        }
    }
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = project.name
            isStatic = false
            export(libs.kotlinx.datetime)
            export(libs.kmm.resources)
            export(libs.kmm.resources.graphics)
            export(projects.core)
            export(projects.core.domain)
            export(projects.core.presentation)
            export(projects.designsystem)
            export(projects.feature.authorization.presentation)
            export(projects.feature.dashboard.presentation)
            export(projects.feature.settings.presentation)
            export(projects.feature.business.presentation)
            export(projects.feature.clients.presentation)
            export(projects.feature.services.presentation)
            export(projects.feature.appointments.presentation)
            export(projects.feature.employees.presentation)
            export(projects.library.money)
            export(projects.library.credentials.api)
            export(projects.library.biometry.api)
            export(projects.library.notifications.api)
            export(projects.library.crashreporting.api)
            export(projects.library.picker)
        }
    }
}
