plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "co.check2go"
    compileSdk = 37

    defaultConfig {
        applicationId = "co.check2go"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0-dev"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.activity:activity-compose:1.13.0")

    val composeBom = platform("androidx.compose:compose-bom:2026.08.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

val localizationKeyPattern = Regex("""<string\\s+name=\"([^\"]+)\"""")
tasks.register("verifyLocalizationKeys") {
    group = "verification"
    description = "Ensures every bundled English string has a Russian translation."
    doLast {
        fun keys(path: String): Set<String> = file(path).readText().let { source ->
            localizationKeyPattern.findAll(source).map { it.groupValues[1] }.toSet()
        }
        val english = keys("src/main/res/values/strings.xml")
        val russian = keys("src/main/res/values-ru/strings.xml")
        check(english == russian) {
            "Localization keys differ. Missing Russian: ${english - russian}; unexpected Russian: ${russian - english}"
        }
    }
}
tasks.named("check").configure { dependsOn("verifyLocalizationKeys") }
