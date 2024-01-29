plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
}

val moduleName = "singledi"

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
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.core.ktx)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.exifinterface)
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