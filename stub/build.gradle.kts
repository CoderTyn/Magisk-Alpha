plugins {
    id("com.android.application")
    id("io.michaelrocks.paranoid")
}

paranoid {
    obfuscationSeed = if (RAND_SEED != 0) RAND_SEED else null
    includeSubprojects = true
}

android {
    defaultConfig {
        applicationId = "com.topjohnwu.magisk"
        versionCode = 1
        versionName = "1.0"
        buildConfigField("int", "STUB_VERSION", Config.stubVersion)
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles("proguard-rules.pro")
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

setupStub()

dependencies {
    implementation(project(":app:shared"))
}
