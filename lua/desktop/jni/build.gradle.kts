plugins {
    id("java-library")
}

val moduleName = "lua-desktop-jni"

base {
    archivesName.set(moduleName)
}

val nativeRoot = file("$projectDir/../../builder/build/c++/libs")
val nativePaths = listOf(
    "$nativeRoot/windows/vc/jni/lua64.dll",
    "$nativeRoot/linux/jni/liblua64.so",
    "$nativeRoot/mac/jni/liblua64.dylib",
    "$nativeRoot/mac/arm/jni/libluaarm64.dylib"
)

tasks.named<Jar>("jar") {
    from(provider {
        nativePaths.map(::file).filter { it.exists() }
    })
}

dependencies {
    api(project(":lua:shared:jni"))

    runtimeOnly(libs.bundles.jParserDesktopJniNatives)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
