import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    id("org.jetbrains.kotlin.jvm").version("1.3.50")
}

val dataforgeVersion by extra("0.1.4")


repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
    maven(url = "https://kotlin.bintray.com/kotlinx" )
    maven("https://dl.bintray.com/mipt-npm/dataforge")
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("hep.dataforge:dataforge-output-jvm:$dataforgeVersion")
    implementation("hep.dataforge:dataforge-context-jvm:$dataforgeVersion")
    implementation("hep.dataforge:dataforge-workspace-jvm:$dataforgeVersion")
    implementation("hep.dataforge:dataforge-meta-jvm:$dataforgeVersion")
    implementation("hep.dataforge:dataforge-io-jvm:$dataforgeVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.15")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.13.0")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
