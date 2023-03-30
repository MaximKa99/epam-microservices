import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    kotlin("plugin.jpa") version "1.8.0"
    kotlin("plugin.spring") version "1.8.0"
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("io.swagger.swaggerhub") version "1.0.1"
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
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.retry:spring-retry")
    runtimeOnly("org.springframework.boot:spring-boot-starter-aop")

    implementation("io.swagger.core.v3:swagger-annotations:2.2.8")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")

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

tasks.named("compileKotlin") {
    dependsOn("openApiGenerateAll")
}

val apiList = listOf("CommonModel", "ResourceApi")

val generateTasks = apiList.map {
    tasks.register(it + "_generate", GenerateTask::class.java) {
        generatorName.set("kotlin-spring")
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
            "documentationProvider" to "none"
        ))
    }
}

val downloadSpecTasks = apiList.map {
    tasks.register(it + "_download", io.swagger.swaggerhub.tasks.DownloadTask::class.java) {
        api = it
        owner = "MaximKa99"
        version = "1.0"
        outputFile = "$rootDir/resource-service/api/$it.yaml"
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
    srcDirs("$buildDir/generated/src/main/kotlin")
}

tasks.swaggerhubUpload {
    api = "ResourceApi"
    owner = "MaximKa99"
    version = "1.0"
    inputFile = "$rootDir/resource-service/api/ResourceApi.yaml"
    token = "21eb0ad4-4a4c-4ac0-97a3-345609256a34"
    format = "yaml"
}

application {
    mainClass.set("com.epam.AppKt")
}

tasks.withType<Test> {
    useJUnitPlatform()
}