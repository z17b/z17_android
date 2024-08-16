plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("maven-publish")
    alias(libs.plugins.compose.compiler)
}

val moduleName = "views"

android {
    namespace = libs.versions.libName.get() + "." + moduleName

    group = libs.versions.libName.get()
    version = libs.versions.versionName.get()

    resourcePrefix(moduleName)
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        //isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

composeCompiler {
    enableStrongSkippingMode = true

    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>(moduleName) {
                groupId = libs.versions.libName.get()
                artifactId = moduleName
                version = libs.versions.versionName.get()
                from(components["release"])
            }
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    //coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    implementation(libs.app.compat)
    implementation(libs.material)

    implementation(libs.zxing.embedded) {
        isTransitive = false
    }
    implementation(libs.zxing.core)

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

    implementation(libs.compose.coil)
    implementation(libs.coil.gif)
    implementation(libs.coil.video)

    implementation(libs.accompanist.permissions)

    implementation(libs.constraintlayout)

    implementation(libs.compose.ui.test)
    implementation(libs.compose.ui.test.junit)
    implementation(libs.compose.ui.test.manifest)

    implementation(libs.markdown.core)
    implementation(libs.markdown.editor)
    implementation(libs.markdownhtml)
    implementation(libs.markdown.ext.latex)
    implementation(libs.markdown.linkify)
    implementation(libs.markdown.ext.strikethrough)
    implementation(libs.markdown.ext.tasklist)

    implementation(libs.lottie.compose)

    implementation(libs.media3.exo.player)
    implementation(libs.media3.mediasession)
    implementation(libs.media3.ui)
    implementation(libs.media3.dash)
    implementation(libs.media3.hls)
    implementation(libs.media3.transformer)
    implementation(libs.media3.effect)
    implementation(libs.media3.common)

    implementation(libs.osmdroid)

    implementation(libs.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.camera.core)
    implementation(libs.camera.extensions)
    implementation(libs.camera.video)

    implementation(libs.gson)

    implementation(libs.exifinterface)

    api(project(":singledi"))
    api(project(":compress"))
}