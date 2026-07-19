plugins {
    id("java-library")
}

dependencies {
    implementation(project(":samples:basic:shared"))
    api("io.github.libfdx:application:${LibExt.libfdxVersion}")
    implementation("io.github.libfdx:graphics:${LibExt.libfdxVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java25Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java25Target)
}
