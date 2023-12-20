plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

val moduleName = "preferences"

android {
    namespace = libs.versions.libName.get() + "." + moduleName

    group = libs.versions.libName.get()
    version = libs.versions.versionName.get()

    resourcePrefix(moduleName)
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
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
}

dependencies {
    implementation(libs.data.store)
    implementation(libs.kotlin.serialization)
    implementation(libs.crypto)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>(moduleName) {
                groupId = libs.versions.libName.get()
                artifactId = moduleName
                version = libs.versions.versionName.get()
            }
        }
    }
}