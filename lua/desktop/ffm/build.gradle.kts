plugins {
    id("java-library")
    id("maven-publish")
}

val moduleName = "lua-ffm"
val luaGenerationTask = ":lua:builder:lua_build_project"

val windowsFile = "$projectDir/../../builder/build/c++/libs/windows/vc/ffm/lua64.dll"
val linuxFile = "$projectDir/../../builder/build/c++/libs/linux/ffm/liblua64.so"
val macFile = "$projectDir/../../builder/build/c++/libs/mac/ffm/liblua64.dylib"
val macArmFile = "$projectDir/../../builder/build/c++/libs/mac/arm/ffm/libluaarm64.dylib"

tasks.jar {
    from(windowsFile, linuxFile, macFile, macArmFile)
}

dependencies {
    api("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:api-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-desktop-ffm:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-desktop-ffm_windows_x64:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-desktop-ffm_linux_x64:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-desktop-ffm_mac_x64:${LibExt.jParserVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-desktop-ffm_mac_arm64:${LibExt.jParserVersion}")
}

tasks.named("compileJava") {
    dependsOn(luaGenerationTask)
}

tasks.named("javadoc") {
    dependsOn(luaGenerationTask)
}

tasks.matching { it.name == "sourcesJar" }.configureEach {
    dependsOn(luaGenerationTask)
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java"
        project.delete(files(srcPath))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java25Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java25Target)
}

java {
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
