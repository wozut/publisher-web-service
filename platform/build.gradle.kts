plugins {
    `java-platform`
}

javaPlatform.allowDependencies()

dependencies {
    api(platform("io.arrow-kt:arrow-stack:${Versions.ARROW}"))

    constraints {
        api("io.arrow-kt:arrow-core")
        api("com.google.code.gson:gson:${Versions.GSON}")
        api("jakarta.inject:jakarta.inject-api:${Versions.JAKARTA_INJECT}")
        api("org.slf4j:slf4j-api:${Versions.SLF4J}")

        api("org.junit.jupiter:junit-jupiter:${Versions.JUNIT}")
        api("org.junit.platform:junit-platform-suite:${Versions.JUNIT_PLATFORM}")
        api("org.assertj:assertj-core:${Versions.ASSERTJ}")
        api("io.mockk:mockk:${Versions.MOCKK}")
    }
}
