@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "1.7.20"
}

val moduleName = "app"

android {
    namespace = libs.versions.libName.get() + "." + moduleName

    compileSdk = libs.versions.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.buildTools.get()

    defaultConfig {
        applicationId = libs.versions.libName.get() + "." + moduleName
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(libs.core.ktx)
    implementation(libs.app.compat)
    implementation(libs.material)

    implementation(libs.activity.ktx)

    implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))
    implementation(libs.compose.animation)
    implementation(libs.compose.animation.core)
    implementation(libs.compose.animation.graphics)
    implementation(libs.compose.foundation)
    implementation(libs.compose.foundation.layout)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.material.ripple)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.size.window)
    implementation(libs.compose.runtime)
    implementation(libs.compose.runtime.live.data)
    implementation(libs.compose.runtime.saveable)
    implementation(libs.compose.ui)
    implementation(libs.compose.geometry)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.text)
    implementation(libs.compose.ui.google.font)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.data)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.unit)
    implementation(libs.compose.ui.util)
    implementation(libs.compose.view.binding)
    implementation(libs.compose.lifecycle)

    implementation(libs.compose.navigation)
    implementation(libs.compose.material.theme)
    implementation(libs.compose.activity)
    implementation(libs.compose.view.model)
    implementation(libs.compose.paging)

    api(project(":views"))
}
