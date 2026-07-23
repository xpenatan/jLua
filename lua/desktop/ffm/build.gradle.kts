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
    api(libs.jParserLoaderCore)
    api(libs.jParserApiCore)
    api(libs.jParserRuntimeDesktopFfm)
    runtimeOnly(libs.bundles.jParserDesktopFfmNatives)
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
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

java {
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
