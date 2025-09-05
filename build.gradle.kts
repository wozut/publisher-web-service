buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

tasks.register("setupGitHooks", Exec::class) {
    description = "Setup Git hooks for all team members"
    group = "setup"
    commandLine("./scripts/setup-git-hooks.sh")
    val ciEnvironmentVariable = System.getenv("CI")
    onlyIf { ciEnvironmentVariable == null || ciEnvironmentVariable != "true" }
}

tasks.named("build") {
    dependsOn("setupGitHooks")
}
