plugins {
    id("java")
}

val mainClassName = "Build"

dependencies {
    implementation(project(":lua:base"))
    implementation(libs.bundles.jParserBuilder)
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.register<JavaExec>("lua_build_project") {
    group = "lua"
    description = "Generate Lua Java bindings"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_ffm", "gen_jni", "gen_web", "gen_teavm_c")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_web") {
    group = "lua"
    description = "Build Web native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_web", "web")
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

tasks.register<JavaExec>("lua_build_project_windows64_teavm_c") {
    group = "lua"
    description = "Build Windows x64 TeaVM C native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_teavm_c", "windows64_teavm_c")
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

tasks.register<JavaExec>("lua_build_project_linux64_teavm_c") {
    group = "lua"
    description = "Build Linux x64 TeaVM C native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_teavm_c", "linux64_teavm_c")
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

tasks.register<JavaExec>("lua_build_project_mac64_teavm_c") {
    group = "lua"
    description = "Build macOS x64 TeaVM C native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_teavm_c", "mac64_teavm_c")
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

tasks.register<JavaExec>("lua_build_project_macArm_teavm_c") {
    group = "lua"
    description = "Build macOS arm64 TeaVM C native library"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_teavm_c", "macArm_teavm_c")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("lua_build_project_android") {
    group = "lua"
    description = "Build Android JNI native libraries"
    mainClass.set(mainClassName)
    args = mutableListOf("gen_jni", "android_jni")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.matching {
    it.name.startsWith("lua_build_project")
}.configureEach {
    dependsOn(":lua:download:lua_download_source")
}
