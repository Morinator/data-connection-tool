plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

dependencies {

    implementation(project(":datatransformlang"))

    implementation("aws.smithy.kotlin:http-client-engine-okhttp4:1.3.26")
    implementation("aws.sdk.kotlin:s3:1.3.81")

    implementation("com.networknt:json-schema-validator:1.0.76")

    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.18.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.xmlunit:xmlunit-assertj:2.9.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
}

configurations.all {
    resolutionStrategy {
        // Force resolve to OkHttp 4.x
        force("com.squareup.okhttp3:okhttp:4.12.0") // or whichever version you are using...
    }
    exclude(group = "com.squareup.okhttp3", module = "okhttp-coroutines") // Exclude dependency on okhttp-coroutines, which is introduced in 5.0.0-alpha.X
}

tasks.withType<Test> {
    useJUnitPlatform()
}