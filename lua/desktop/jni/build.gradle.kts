plugins {
    id("java-library")
}

val moduleName = "lua-desktop-jni"
group = "${LibExt.groupId}.desktop"

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

    runtimeOnly("com.github.xpenatan.jParser:runtime-desktop-jni_windows_x64:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-desktop-jni_linux_x64:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-desktop-jni_mac_x64:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-desktop-jni_mac_arm64:${LibExt.jParserVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java8Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java8Target)
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            groupId = LibExt.groupId
            version = LibExt.libVersion
            from(components["java"])
        }
    }
}
