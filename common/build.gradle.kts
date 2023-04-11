import plugin.ApiDownloadTask

plugins {
    kotlin("jvm") version "1.8.10"
    id("io.swagger.swaggerhub") version "1.0.1"
    id("org.openapi.generator") version "6.3.0"
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

tasks.register("downloadApis", ApiDownloadTask::class.java) {
    api = "CommonModel"
    token = "GHSAT0AAAAAACAZQZBZFW367SWQA36MXINSZBVEPMQ"
    outputFile = "$rootDir/common/api/CommonModel2.yaml"
}

sourceSets["main"].java {
    srcDirs("$buildDir/generated/src/main/kotlin/com/epam/dto")
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("$rootDir/common/api/CommonModel.yaml")
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

tasks.swaggerhubUpload {
    api = "CommonModel"
    owner = "MaximKa99"
    version = "1.0"
    inputFile = "$rootDir/common/api/CommonModel.yaml"
    token = "21eb0ad4-4a4c-4ac0-97a3-345609256a34"
    format = "yaml"
}

tasks.withType<Test> {
    useJUnitPlatform()
}