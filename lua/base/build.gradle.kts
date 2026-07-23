plugins {
    id("java")
}

dependencies {
    implementation(libs.bundles.jParserBase)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
