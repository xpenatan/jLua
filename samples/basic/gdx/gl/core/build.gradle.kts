plugins {
    id("java-library")
}

dependencies {
    implementation(project(":samples:basic:shared"))
    api(libs.gdxCore)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
