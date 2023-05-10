plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    kotlin("plugin.jpa") version "1.8.0"
    kotlin("plugin.spring") version "1.8.0"
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.ajoberstar.grgit") version "4.1.1"
    id("org.openapi.generator") version "6.3.0"
    application
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.5")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-sleuth-zipkin")
    implementation("org.springframework.security:spring-security-oauth2-client")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jolokia:jolokia-core")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.8")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("org.postgresql:postgresql:42.5.3")
    implementation("net.logstash.logback:logstash-logback-encoder:7.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testImplementation("org.testcontainers:postgresql:1.17.6")
}

tasks.named("compileKotlin") {
    dependsOn("downloadAllSpecs")
    dependsOn("generateAllSpecs")
}

val apiList = listOf("CommonModel", "StorageApi")

val generateTasks = apiList.map {
    tasks.register(it + "_generate", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class.java) {
        generatorName.set("spring")
        inputSpec.set("$rootDir/storage-service/api/$it.yaml")
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

val downloadTasks = apiList.map {
    tasks.register("download_$it", plugin.ApiDownloadTask::class.java) {
        api = it
        outputFile = "$rootDir/storage-service/api/$it.yaml"
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


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("com.epam.AppKt")
}