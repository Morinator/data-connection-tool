dependencies {
    implementation("com.opencsv:opencsv:5.9")
    implementation("com.jayway.jsonpath:json-path:2.9.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.xmlunit:xmlunit-assertj:2.9.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

}

tasks.withType<Test> {
    useJUnitPlatform()
}