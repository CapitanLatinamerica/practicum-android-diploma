// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("convention.detekt")
}
buildscript {
    repositories {
        google()
    }
    dependencies {
        val nav_version = "2.9.3"
        classpath(libs.navigation.safe.args.gradle.plugin)
    }
}
