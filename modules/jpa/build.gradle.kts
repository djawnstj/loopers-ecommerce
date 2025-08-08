plugins {
    id("org.jetbrains.kotlin.plugin.jpa")
    `java-test-fixtures`
}

dependencies {
    // jpa
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    // jdbc-mysql
    runtimeOnly("com.mysql:mysql-connector-j")
    // kotlin-jdsl
    api("com.linecorp.kotlin-jdsl:jpql-dsl:3.5.5")
    api("com.linecorp.kotlin-jdsl:jpql-render:3.5.5")
    api("com.linecorp.kotlin-jdsl:spring-data-jpa-support:3.5.5")

    testImplementation("org.testcontainers:mysql")

    testFixturesImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testFixturesImplementation("org.testcontainers:mysql")
}
