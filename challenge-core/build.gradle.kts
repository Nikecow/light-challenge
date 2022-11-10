plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":challenge-data"))

    // Kotlin libs
    implementation(kotlin("stdlib"))

    // DB
    implementation(Libraries.exposed)
    implementation(Libraries.sqlite_database)

    // Logging
    implementation(Libraries.slf_log4j)
    implementation(Libraries.microutils_logging)

    // Mockito Kotlin
    testImplementation(Libraries.mockito_kotlin)
    testImplementation(Libraries.mockito_inline)

    // AssertK
    testImplementation(Libraries.assertk)

    // Junit
    testImplementation(Libraries.junit_jupiter_api)
    testRuntimeOnly(Libraries.junit_jupiter_engine)
}
