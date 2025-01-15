plugins {
  kotlin("jvm") version "2.0.21"
  kotlin("plugin.serialization") version "2.0.21"
}

group = "org.example"

version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation("com.google.code.gson:gson:2.10.1")

  testImplementation(kotlin("test"))
}

tasks.test { useJUnitPlatform() }

tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
  compilerOptions {
    freeCompilerArgs.add("-Xdebug")
  }
}

kotlin { jvmToolchain(17) }
