plugins {
    id("org.jetbrains.kotlin.plugin.jpa")
}

dependencies {
    // add-ons
    implementation(project(":modules:jpa"))
    implementation(project(":supports:jackson"))

    // Spring Batch
    implementation("org.springframework.boot:spring-boot-starter-batch")

    // test-fixtures
    testImplementation(testFixtures(project(":modules:jpa")))
}
