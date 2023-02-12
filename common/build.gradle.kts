plugins {
    kotlin("jvm") version "1.8.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("io.mockk:mockk:1.13.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}