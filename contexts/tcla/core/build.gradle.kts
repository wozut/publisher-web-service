plugins {
    id("kotlin-library-conventions")
    id("org.springframework.boot") version "3.5.4" apply false
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "1.9.25"
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

base {
    archivesName.set("tcla.core")
}

dependencies {
    implementation("jakarta.inject:jakarta.inject-api")
    implementation(project(":libraries:transactional"))
    implementation(project(":libraries:uuid-validation"))
    implementation(project(":libraries:search"))
    implementation(project(":libraries:logging"))

    implementation(project(":contexts:authentication:core"))
    implementation(project(":contexts:accounts:core"))
    implementation(project(":contexts:communications:core"))

    api(project(":libraries:time"))

    testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("io.cucumber:cucumber-java8:7.14.0")
    testImplementation("io.cucumber:cucumber-java:7.14.0")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.14.0")

    testFixturesImplementation(platform("io.arrow-kt:arrow-stack:2.1.2"))
    testFixturesImplementation("io.arrow-kt:arrow-core")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("cucumber.execution.parallel.enabled", true)
    systemProperty("cucumber.junit-platform.naming-strategy", "long")
    systemProperty("cucumber.publish.quiet", "true")
}
