plugins {
    kotlin("jvm")
}

dependencies {
    // Kotlin libs
    implementation(kotlin("stdlib"))

    // Logging
    implementation(Libraries.slf_log4j)
    implementation(Libraries.microutils_logging)

    // DB
    implementation(Libraries.exposed)
    implementation(Libraries.sqlite_database)

    // Mockito Kotlin
    testImplementation(Libraries.mockito_kotlin)
    testImplementation(Libraries.mockito_inline)

    // Junit
    testImplementation(Libraries.junit_jupiter_api)
    testRuntimeOnly(Libraries.junit_jupiter_engine)
}
