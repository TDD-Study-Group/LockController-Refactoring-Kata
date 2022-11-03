plugins {
    kotlin("jvm") version "1.7.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.kotest:kotest-assertions-core:5.5.3")
    testImplementation("io.mockk:mockk-jvm:1.13.2")
}

tasks{
    test{
        useJUnitPlatform()
    }
}
