plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.google.dagger.hilt.android") version "2.49" apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.23" apply false
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15") // Google services plugin
    }
}

allprojects {
    // No need to add repositories here anymore since it's handled in settings.gradle.kts
}
