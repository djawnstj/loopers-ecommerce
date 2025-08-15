plugins {
    id("java")
}

group = "com.loopers"
version = "eac6e20"

repositories {
    mavenCentral()
}

dependencies {
    // add-ons
    implementation(project(":modules:redis"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // test-fixtures
    testImplementation(testFixtures(project(":modules:redis")))
}

tasks.test {
    useJUnitPlatform()
}
