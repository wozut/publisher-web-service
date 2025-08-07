import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("kotlin-library-conventions")
    id("org.springframework.boot") version "3.5.4" apply false
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "2.2.0"
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

archivesName = "accounts.core"

dependencies {
    implementation("jakarta.inject:jakarta.inject-api")
    implementation(project(":libraries:transactional"))
    implementation(project(":libraries:logging"))
    implementation(project(":contexts:authentication:core"))

    testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    testFixturesImplementation(platform("io.arrow-kt:arrow-stack:1.2.1"))
    testFixturesImplementation("io.arrow-kt:arrow-core")
}
