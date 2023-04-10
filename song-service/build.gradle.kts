import io.swagger.swaggerhub.tasks.DownloadTask
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    kotlin("plugin.jpa") version "1.8.10"
    kotlin("plugin.spring") version "1.8.10"
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("io.swagger.swaggerhub") version "1.0.1"
    id("org.openapi.generator") version "6.3.0"
    application
}

group = "com.epam"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.5")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("io.swagger.core.v3:swagger-annotations:2.2.8")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")

    implementation("org.postgresql:postgresql:42.5.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("au.com.dius.pact.provider:junit5spring:4.3.7")
    testImplementation("io.cucumber:cucumber-java:7.11.1")
    testImplementation("io.cucumber:cucumber-junit:7.11.1")
    testImplementation("io.cucumber:cucumber-spring:7.11.1")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testImplementation("org.testcontainers:postgresql:1.17.6")
}

tasks.named("compileKotlin") {
    dependsOn("openApiGenerateAll")
}

val apiList = listOf("CommonModel", "SongApi")

val generateTasks = apiList.map {
    tasks.register(it + "_generate", GenerateTask::class.java) {
        generatorName.set("spring")
        inputSpec.set("$rootDir/song-service/api/$it.yaml")
        outputDir.set("$buildDir/generated")
        modelPackage.set("com.epam.dto")
        apiPackage.set("com.epam.api")
        generateModelTests.set(false)
        generateApiTests.set(false)
        configOptions.set(mutableMapOf(
            "interfaceOnly" to "true",
            "useTags" to "true",
            "useSwaggerUI" to "false"
        ))
    }
}

val downloadSpecTasks = apiList.map {
    tasks.register(it + "_download", DownloadTask::class.java) {
        api = it
        owner = "MaximKa99"
        version = "1.0"
        outputFile = "$rootDir/song-service/api/$it.yaml"
        token = "21eb0ad4-4a4c-4ac0-97a3-345609256a34"
        format = "yaml"
    }
}

tasks.register("swaggerhubDownloadAll") {
    group = "swaggerhub"

    dependsOn(downloadSpecTasks)
}

tasks.register("openApiGenerateAll") {
    group = "openapi tools"
    dependsOn(generateTasks)
}



sourceSets["main"].java {
    srcDirs("$buildDir/generated/src/main/java")
}

tasks.swaggerhubUpload {
    api = "SongApi"
    owner = "MaximKa99"
    version = "1.0"
    inputFile = "$rootDir/song-service/api/SongApi.yaml"
    token = "21eb0ad4-4a4c-4ac0-97a3-345609256a34"
    format = "yaml"
}

application {
    mainClass.set("com.epam.AppKt")
}

tasks.withType<Test> {
    useJUnitPlatform()
}