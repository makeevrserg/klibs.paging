@file:Suppress("UnusedPrivateProperty")

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("ru.astrainteractive.gradleplugin.java.version")
    id("ru.astrainteractive.gradleplugin.android.sdk")
    id("ru.astrainteractive.gradleplugin.publication")
    id("ru.astrainteractive.gradleplugin.android.namespace")
    id("ru.astrainteractive.klibs.rules.gradle.linter")
    id("ru.astrainteractive.gradleplugin.rootinfo")
}

linter {
    unitKind.set("api")
    rootPackage.set("ru.astrainteractive.klibs.paging")
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    freeCompilerArgs.add("-Xexplicit-api=strict")
}

kotlin {
    explicitApi()
    jvm()
    androidLibrary {}
    js(IR) {
        browser()
        nodejs()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()
    watchosX64()
    watchosArm64()
    watchosSimulatorArm64()
    linuxX64()
    macosX64()
    macosArm64()
    mingwX64()

    sourceSets {
        // Main source sets
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.test.turbine)
                implementation(libs.kotlin.coroutines.test)
            }
        }
    }
}
