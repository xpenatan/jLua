plugins {
    id("java-library")
}

dependencies {
    api("io.github.libfdx:application:${LibExt.libfdxVersion}")
    implementation("io.github.libfdx:graphics:${LibExt.libfdxVersion}")
    implementation(project(":extensions:lua-ext"))
    compileOnly(project(":lua:lua-core"))
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java25Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java25Target)
}
