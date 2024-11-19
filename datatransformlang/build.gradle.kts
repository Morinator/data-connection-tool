dependencies {
    implementation("com.opencsv:opencsv:5.9")
    implementation("com.jayway.jsonpath:json-path:2.9.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.18.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.1")


    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.xmlunit:xmlunit-assertj:2.9.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

tasks.withType<Test> {
    useJUnitPlatform()
}