import de.undercouch.gradle.tasks.download.Download
import org.gradle.kotlin.dsl.support.unzipTo

plugins {
    id("java")
    id("de.undercouch.download") version("5.5.0")
}

val mainClassName = "Build"

dependencies {
    implementation(project(":lua:lua-base"))
    implementation("com.github.xpenatan.jParser:gen-core:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:gen-build:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:gen-build-tool:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:gen-ffm:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:gen-web:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:gen-jni:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:gen-idl:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:runtime-core:${LibExt.jParserVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java25Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java25Target)
}

val buildDir = layout.buildDirectory.get().asFile
val zippedPath = "$buildDir/lua-source.zip"
val sourcePath = "$buildDir/lua-source"
val sourceDestination = "$buildDir/lua/"

tasks.register<Download>("lua_download_source") {
    group = "lua"
    description = "Download lua source"
    src("https://github.com/lua/lua/archive/refs/tags/v5.4.6.zip")
    dest(File(zippedPath))
    doLast {
        unzipTo(File(sourcePath), dest)
        copy{
            from("$sourcePath/lua-5.4.6")
            into(sourceDestination)
        }
        delete(sourcePath)
        delete(zippedPath)
    }
}

tasks.register<JavaExec>("lua_build_project") {
    group = "lua"
    description = "Generate Lua Java bindings"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_ffm", "gen_jni", "gen_web")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_web") {
    group = "lua"
    description = "Build Web native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_web", "web")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_generate_web") {
    group = "lua"
    description = "Generate Web Java bindings"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_web")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_windows64") {
    group = "lua"
    description = "Build Windows x64 JNI native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_jni", "windows64_jni")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_windows64_jni") {
    group = "lua"
    description = "Build Windows x64 JNI native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_jni", "windows64_jni")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_windows64_ffm") {
    group = "lua"
    description = "Build Windows x64 FFM native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_ffm", "windows64_ffm")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_linux64") {
    group = "lua"
    description = "Build Linux x64 JNI native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_jni", "linux64_jni")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_linux64_jni") {
    group = "lua"
    description = "Build Linux x64 JNI native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_jni", "linux64_jni")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_linux64_ffm") {
    group = "lua"
    description = "Build Linux x64 FFM native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_ffm", "linux64_ffm")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_mac64") {
    group = "lua"
    description = "Build macOS x64 JNI native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_jni", "mac64_jni")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_mac64_jni") {
    group = "lua"
    description = "Build macOS x64 JNI native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_jni", "mac64_jni")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_mac64_ffm") {
    group = "lua"
    description = "Build macOS x64 FFM native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_ffm", "mac64_ffm")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_macArm") {
    group = "lua"
    description = "Build macOS arm64 JNI native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_jni", "macArm_jni")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_macArm_jni") {
    group = "lua"
    description = "Build macOS arm64 JNI native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_jni", "macArm_jni")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_macArm_ffm") {
    group = "lua"
    description = "Build macOS arm64 FFM native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_ffm", "macArm_ffm")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_android") {
    group = "lua"
    description = "Build Android JNI native libraries"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_jni", "android_jni")
    classpath = sourceSets["main"].runtimeClasspath
}
