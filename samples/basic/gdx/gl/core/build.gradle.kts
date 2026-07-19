plugins {
    id("java-library")
}

dependencies {
    implementation(project(":samples:basic:shared"))
    api("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java8Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java8Target)
}
