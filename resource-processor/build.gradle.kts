import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    kotlin("plugin.spring") version "1.8.0"
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.ajoberstar.grgit") version "4.1.1"
    id("org.openapi.generator") version "6.3.0"
    application
}

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
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.retry:spring-retry")
    runtimeOnly("org.springframework.boot:spring-boot-starter-aop")

    implementation("software.amazon.awssdk:sqs:2.19.26")

    implementation("org.apache.tika:tika-parsers:1.24.1")

    implementation("io.swagger.core.v3:swagger-annotations:2.2.8")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testImplementation("com.github.tomakehurst:wiremock:2.27.2")

    testImplementation("au.com.dius.pact.consumer:junit5:4.3.7")
}

tasks.named("compileKotlin") {
    dependsOn("downloadAllSpecs")
    dependsOn("generateAllSpecs")
}

val apiList = listOf("CommonModel", "ResourceApi", "SongApi")

val generateTasks = apiList.map {
    tasks.register(it + "_generate", GenerateTask::class.java) {
        generatorName.set("spring")
        inputSpec.set("$rootDir/resource-service/api/$it.yaml")
        outputDir.set("$buildDir/generated")
        modelPackage.set("com.epam.dto")
        apiPackage.set("com.epam.api")
        generateModelTests.set(false)
        generateApiTests.set(false)
        configOptions.set(mutableMapOf(
            "interfaceOnly" to "true",
            "useTags" to "true",
            "useSwaggerUI" to "false",
            "documentationProvider" to "none",
            "library" to "spring-cloud"
        ))
    }
}

val downloadTasks = apiList.map {
    tasks.register("download_$it", plugin.ApiDownloadTask::class.java) {
        api = it
        outputFile = "$rootDir/resource-service/api/$it.yaml"
        branch = grgit.branch.current().name
    }
}

tasks.register("generateAllSpecs") {
    group = "apiHub"

    dependsOn(generateTasks)
}

tasks.register("downloadAllSpecs") {
    group = "apiHub"

    dependsOn(downloadTasks)
}

sourceSets["main"].java {
    srcDirs("$buildDir/generated/src/main/java")
}

application {
    mainClass.set("com.epam.AppKt")
}

tasks.withType<Test> {
    useJUnitPlatform()
}