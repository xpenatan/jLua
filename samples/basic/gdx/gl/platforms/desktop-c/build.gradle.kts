import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

fun currentDesktopPlatformName(): String {
    val os = DefaultNativePlatform.getCurrentOperatingSystem()
    val archName = DefaultNativePlatform.getCurrentArchitecture().name.lowercase()
    val isX64 = archName == "x86-64" || archName == "x86_64" || archName == "amd64"
    val isArm64 = archName == "aarch64" || archName == "arm64"
    return when {
        os.isWindows && isX64 -> "windows_x64"
        os.isLinux && isX64 -> "linux_x64"
        os.isMacOsX && isArm64 -> "mac_arm64"
        os.isMacOsX && isX64 -> "mac_x64"
        else -> throw GradleException("Unsupported desktop TeaVM C platform: ${os.name} $archName")
    }
}

val currentDesktopPlatform = currentDesktopPlatformName()

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":samples:basic:gdx:gl:core"))
    implementation(project(":lua:shared:c"))
    runtimeOnly(project(mapOf(
        "path" to ":lua:desktop:c",
        "configuration" to "nativeRuntime_$currentDesktopPlatform",
    )))
    runtimeOnly("com.github.xpenatan.jParser:runtime-desktop-c_$currentDesktopPlatform:${LibExt.jParserVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java17Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java17Target)
}

gdxTeaVM {
    glfw {
        mainClass.set("lua.sample.gdx.gl.desktop.LuaGdxDesktopCLauncher")
        targetFileName.set("jlua-gdx")
        optimization.set(OptimizationLevel.AGGRESSIVE)
        obfuscated.set(false)
        minHeapSizeMb.set(64)
        maxHeapSizeMb.set(512)
        buildType.set("Debug")
        consoleLog.set(true)
    }
}

tasks.matching { it.name.startsWith("gdx_teavm_glfw_") }.configureEach {
    dependsOn(":lua:shared:c:jar")
    dependsOn(":lua:desktop:c:nativeJar_$currentDesktopPlatform")
}

// This module declares GLFW as its only TeaVM native backend. The plugin normally
// selects a native backend from the requested task name, while the public sample
// aliases below intentionally use the same naming scheme as the other runtimes.
afterEvaluate {
    tasks.named("generateC").configure {
        setOnlyIf("the jLua libGDX sample declares only the GLFW native backend") { true }
    }
}

tasks.register("lua_gdx_desktop_c_build") {
    group = "samples"
    description = "Builds the jLua libGDX OpenGL desktop TeaVM C sample."
    dependsOn("gdx_teavm_glfw_build")
}

tasks.register<Exec>("lua_gdx_desktop_c_run") {
    group = "samples"
    description = "Runs the jLua libGDX OpenGL desktop TeaVM C sample."
    dependsOn("lua_gdx_desktop_c_build")
    val executableSuffix = if(DefaultNativePlatform.getCurrentOperatingSystem().isWindows) ".exe" else ""
    val releaseDir = layout.buildDirectory.dir("dist/glfw/c/release")
    val executableFile = releaseDir.map { it.file("jlua-gdx_debug$executableSuffix") }
    workingDir(releaseDir)
    doFirst {
        val executable = executableFile.get().asFile
        if(!executable.isFile) {
            throw GradleException("Missing generated libGDX desktop C executable: ${executable.absolutePath}")
        }
        val command = mutableListOf(executable.absolutePath)
        val exitAfterFrames = providers.gradleProperty("jlua.sample.exitAfterFrames").orNull
        if(!exitAfterFrames.isNullOrBlank()) {
            command += exitAfterFrames
        }
        commandLine(command)
    }
}
