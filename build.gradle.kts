import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.5.31"
}

group = "me.uj347"
version = "1.0-SNAPSHOT"
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    testImplementation(kotlin("test"))
    kapt ("com.google.dagger:dagger-compiler:2.40.5")
    implementation ("com.google.dagger:dagger:2.40.5")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
}

tasks.test {
    useJUnit()

}
tasks.jar {
//    manifest{
//
//    }
//    configurations["compileClasspath"].forEach { file: File ->
//        from(zipTree(file.absoluteFile))
//    }
//    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}