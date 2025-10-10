plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation("com.squareup.moshi:moshi:${Versions.MOSHI}")
    implementation("com.squareup.moshi:moshi-kotlin:${Versions.MOSHI}")
}
