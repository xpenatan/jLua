plugins {
    id("java-library")
}

dependencies {
    implementation(project(":extensions:lua-ext"))
    compileOnly(project(":lua:core"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
