plugins {
    id("kotlin-library-conventions")
}

dependencies {
    api(platform("com.squareup.okhttp3:okhttp-bom:${Versions.OKHTTP_BOM}"))
    api("com.squareup.okhttp3:okhttp")
}
