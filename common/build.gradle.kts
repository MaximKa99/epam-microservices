import plugin.ApiDownloadTask

plugins {
    kotlin("jvm") version "1.8.10"
    id("org.openapi.generator") version "6.3.0"
    id("org.ajoberstar.grgit") version "4.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("io.swagger.core.v3:swagger-annotations:2.2.8")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")


    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("io.mockk:mockk:1.13.4")
}

tasks.named("compileKotlin") {
    dependsOn("downloadAllSpecs")
    dependsOn("generateAllSpecs")
}

val apis = listOf("CommonModel")

val generateTasks = apis.map {
    tasks.register(it + "_generate", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class.java) {
        generatorName.set("kotlin")
        inputSpec.set("$rootDir/common/api/$it.yaml")
        outputDir.set("$buildDir/generated")
        modelPackage.set("com.epam.dto")
        apiPackage.set("com.epam.api")
        generateModelTests.set(false)
        generateApiTests.set(false)
        configOptions.set(mutableMapOf(
            "interfaceOnly" to "true",
            "useTags" to "true",
            "useSwaggerUI" to "false",
            "serializationLibrary" to "jackson"
        ))
    }
}

val downloadTasks = apis.map {
    tasks.register("download_$it", ApiDownloadTask::class.java) {
        api = it
        outputFile = "$rootDir/common/api/$it.yaml"
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
    srcDirs("$buildDir/generated/src/main/kotlin/com/epam/dto")
}

tasks.withType<Test> {
    useJUnitPlatform()
}