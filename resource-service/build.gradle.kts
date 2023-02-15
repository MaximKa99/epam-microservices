plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    kotlin("plugin.jpa") version "1.8.0"
    kotlin("plugin.spring") version "1.8.0"
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.1.0"
    application
}

group "org.example"
version "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.retry:spring-retry")
    runtimeOnly("org.springframework.boot:spring-boot-starter-aop")

    implementation("software.amazon.awssdk:s3:2.19.26")
    implementation("software.amazon.awssdk:sqs:2.19.26")

    implementation("org.postgresql:postgresql:42.5.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testImplementation("org.testcontainers:postgresql:1.17.6")
    testImplementation("org.testcontainers:localstack:1.17.6")

}

application {
    mainClass.set("com.epam.AppKt")
}

tasks.withType<Test> {
    useJUnitPlatform()
}