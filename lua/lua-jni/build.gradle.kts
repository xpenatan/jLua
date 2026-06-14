plugins {
    id("java-library")
}

val moduleName = "lua-jni"

val windowsFile = "$projectDir/../lua-build/build/c++/libs/windows/vc/jni/lua64.dll"
val linuxFile = "$projectDir/../lua-build/build/c++/libs/linux/jni/liblua64.so"
val macFile = "$projectDir/../lua-build/build/c++/libs/mac/jni/liblua64.dylib"
val macArmFile = "$projectDir/../lua-build/build/c++/libs/mac/arm/jni/libluaarm64.dylib"

tasks.jar {
    from(windowsFile, linuxFile, macFile, macArmFile)
}

dependencies {
    api("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:api-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-jni:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-jni_windows_x64:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-jni_linux_x64:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-jni_mac_x64:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-jni_mac_arm64:${LibExt.jParserVersion}")
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java"
        project.delete(files(srcPath))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java8Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java8Target)
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            group = LibExt.groupId
            version = LibExt.libVersion
            from(components["java"])
        }
    }
}
