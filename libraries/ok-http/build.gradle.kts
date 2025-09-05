plugins {
    id("kotlin-library-conventions")
}

dependencies {
    api(platform("com.squareup.okhttp3:okhttp-bom:5.1.0"))
    api("com.squareup.okhttp3:okhttp")
}
