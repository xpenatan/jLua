plugins {
    id("java-library")
}

dependencies {
    implementation(project(":samples:basic:shared"))
    api(libs.libfdxApplication)
    implementation(libs.libfdxGraphics)
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}
