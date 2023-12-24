plugins {
    kotlin("jvm") version "1.9.0" apply false
    id("com.android.application") version "8.1.2" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.6" apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
