plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

dependencies {

    implementation("com.h2database:h2:2.2.224")
    implementation("org.springframework:spring-jdbc:6.1.15")

    implementation(project(":datatransformlang"))

    implementation("software.amazon.awssdk:s3:2.20.26")

    implementation("com.networknt:json-schema-validator:1.0.76")

    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.18.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
