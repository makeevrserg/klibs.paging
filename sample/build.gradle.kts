@file:Suppress("UnusedPrivateMember")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import ru.astrainteractive.gradleplugin.property.util.requireProjectInfo

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.compose.gradle)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.klibs.rules)
}

linter {
    unitKind.set("context")
    rootPackage.set("ru.astrainteractive.klibs.sample")
}

kotlin {
    jvm()

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.material3)
                implementation(libs.compose.imageloader)
                implementation(libs.ktor.core)
                implementation(libs.klibs.mikro.core)
                implementation(libs.kotlin.serialization.json)
                implementation(libs.kotlin.coroutines.core)
                implementation(project(":paging"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlin.coroutines.swing)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlin.coroutines.test)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "ru.astrainteractive.klibs.sample.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Deb
            )
            packageName = "${requireProjectInfo.group}.sample"
            packageVersion = "1.0.0"
        }
    }
}
