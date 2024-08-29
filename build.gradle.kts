// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.dagger.hilt.android) apply false
    alias(libs.plugins.jetbrains.android) apply false
    alias(libs.plugins.devtools.ksp) apply false
}