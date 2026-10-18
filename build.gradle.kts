// Archivo: build.gradle.kts (Project)

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false // Se declara el plugin de Compose a nivel global
}