import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.compose") version "1.0.0"
    id("com.diffplug.spotless") version "6.1.0"
}

group = "br.com.firstsoft"
version = "0.0.1"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
}

dependencies {
    implementation("net.java.dev.jna:jna-platform:5.9.0")
    implementation("io.ktor:ktor-server-netty:2.0.0-eap-256")
    implementation("io.ktor:ktor-html-builder:2.0.0-eap-256")
    implementation("io.ktor:ktor-server-default-headers:2.0.0-eap-256")
    implementation("io.ktor:ktor-server-call-logging:2.0.0-eap-256")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation(compose.desktop.currentOs)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

sourceSets {
    main {
        java {
            srcDir("src/main/kotlin")
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "kMonitor"
            packageVersion = "0.0.1"
        }
    }
}

spotless {
    format("misc") {
        target("*.gradle", "*.md", ".gitignore")

        trimTrailingWhitespace()
        indentWithTabs()
        endWithNewline()
    }
    kotlin {
        ktlint()
    }
}