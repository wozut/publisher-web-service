plugins {
    `java-platform`
}

javaPlatform.allowDependencies()

dependencies {
    api(platform("io.arrow-kt:arrow-stack:2.1.2"))

    constraints {
        api("io.arrow-kt:arrow-core")
        api("com.google.code.gson:gson:2.13.1")
        api("jakarta.inject:jakarta.inject-api:2.0.1")
        api("org.slf4j:slf4j-api:2.0.9")

        api("org.junit.jupiter:junit-jupiter:5.9.1")
        api("org.junit.platform:junit-platform-suite:1.10.0")
        api("org.assertj:assertj-core:3.24.2")
        api("io.mockk:mockk:1.13.5")
    }
}
