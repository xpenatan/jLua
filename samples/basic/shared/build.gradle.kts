plugins {
    id("java-library")
}

dependencies {
    implementation(project(":extensions:lua-ext"))
    compileOnly(project(":lua:core"))
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java8Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java8Target)
}
