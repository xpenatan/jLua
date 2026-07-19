import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.RelativePath
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
    id("io.github.libfdx")
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
    implementation(project(":samples:basic:fdx:core"))
    implementation(project(":lua:shared:c"))
    runtimeOnly(project(mapOf(
        "path" to ":lua:desktop:c",
        "configuration" to "nativeRuntime_$currentDesktopPlatform",
    )))

    implementation("io.github.libfdx:backend_desktop_c:${LibExt.libfdxVersion}")
    runtimeOnly("io.github.libfdx:gl_desktop_c:${LibExt.libfdxVersion}")
    runtimeOnly("com.github.xpenatan.jParser:runtime-desktop-c_$currentDesktopPlatform:${LibExt.jParserVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java25Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java25Target)
}

libfdx {
    desktopC {
        showConsole.set(providers.gradleProperty("libfdx.desktopC.showConsole")
            .map { value -> value.toBooleanStrictOrNull() ?: value.toBoolean() }
            .orElse(true))

        target("opengl") {
            displayName.set("jLua libFDX desktop C OpenGL sample")
            mainClass.set("lua.sample.fdx.desktop.LuaFdxDesktopCLauncher")
            targetFileName.set("jlua-fdx-desktop-c")
        }
    }
}

tasks.matching { it.name.startsWith("libfdx_desktop_c_") }.configureEach {
    dependsOn(":lua:shared:c:jar")
    dependsOn(":lua:desktop:c:nativeJar_$currentDesktopPlatform")
}

val integrateJParserTeaVMCResources = tasks.register<Copy>("integrateJParserTeaVMCResources") {
    group = "application"
    description = "Adds jParser TeaVM C resources and CMake hooks to the generated native project."
    dependsOn("libfdx_desktop_c_generate_internal")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    outputs.upToDateWhen { false }

    from({
        configurations["runtimeClasspath"].files
            .filter { it.extension.equals("jar", ignoreCase = true) }
            .map { zipTree(it) }
    }) {
        include("external_cpp/**")
        includeEmptyDirs = false
        eachFile {
            relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
        }
    }
    into(layout.buildDirectory.dir("dist/desktop-c/c/external_cpp"))

    doLast {
        val cmakeFile = layout.buildDirectory.file("dist/desktop-c/CMakeLists.txt").get().asFile
        val marker = "# jLua jParser TeaVM C hooks"
        if(!cmakeFile.isFile) {
            throw GradleException("Missing generated desktop C CMake file: ${cmakeFile.absolutePath}")
        }
        val nativeRoot = layout.buildDirectory.dir("dist/desktop-c/c/external_cpp")
            .get().asFile.absolutePath.replace('\\', '/')
        val packagedGlfw =
            "  include_directories(\"$nativeRoot/glfw/include\")\n" +
                "  link_directories(\"$nativeRoot/glfw/lib-vc2022\")"
        val sourceGlfw = """
              include(FetchContent)
              set(GLFW_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
              set(GLFW_BUILD_TESTS OFF CACHE BOOL "" FORCE)
              set(GLFW_BUILD_DOCS OFF CACHE BOOL "" FORCE)
              set(GLFW_INSTALL OFF CACHE BOOL "" FORCE)
              FetchContent_Declare(libfdx_glfw
                URL https://github.com/glfw/glfw/archive/refs/tags/3.4.zip
                DOWNLOAD_EXTRACT_TIMESTAMP TRUE)
              FetchContent_MakeAvailable(libfdx_glfw)
        """.trimIndent()

        var cmakeText = cmakeFile.readText().replace("\r\n", "\n")
        cmakeText = cmakeText
            .replace(
                "cmake_minimum_required(VERSION 3.14)",
                "cmake_minimum_required(VERSION 3.15)\ncmake_policy(SET CMP0091 NEW)"
            )
            .replace(
                "set(CMAKE_C_STANDARD 11)",
                "set(CMAKE_C_STANDARD 11)\nset(CMAKE_MSVC_RUNTIME_LIBRARY \"MultiThreaded\")"
            )
            .replace(packagedGlfw, sourceGlfw)
            .replace(
                "target_link_libraries(jlua-fdx-desktop-c PRIVATE glfw3)",
                "target_link_libraries(jlua-fdx-desktop-c PRIVATE glfw)"
            )

        if(!cmakeText.contains(marker)) {
            cmakeText += """

                $marker
                set(JPARSER_TEAVMC_APP_TARGET jlua-fdx-desktop-c)
                set(JPARSER_TEAVMC_GENERATED_SOURCE_ROOT "${'$'}{CMAKE_CURRENT_SOURCE_DIR}/c/src")
                file(GLOB JPARSER_TEAVMC_POST_TARGET_HOOKS
                  "${'$'}{CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/cmake/post_target/*.cmake")
                list(SORT JPARSER_TEAVMC_POST_TARGET_HOOKS)
                foreach(JPARSER_TEAVMC_POST_TARGET_HOOK IN LISTS JPARSER_TEAVMC_POST_TARGET_HOOKS)
                  include("${'$'}{JPARSER_TEAVMC_POST_TARGET_HOOK}")
                endforeach()
            """.trimIndent() + "\n"
        }
        cmakeFile.writeText(cmakeText)
    }
}

tasks.matching {
    it.name.startsWith("libfdx_desktop_c_build_") && it.name.endsWith("_internal")
}.configureEach {
    dependsOn(integrateJParserTeaVMCResources)
}

tasks.matching {
    it.name.startsWith("libfdx_desktop_c_") &&
        !it.name.endsWith("_internal") &&
        (it.name.contains("_generate_") || it.name.contains("_build_") || it.name.contains("_run_"))
}.configureEach {
    dependsOn(integrateJParserTeaVMCResources)
}

tasks.register("lua_fdx_desktop_gl_c_build") {
    group = "samples"
    description = "Builds the jLua libFDX OpenGL desktop TeaVM C sample."
    dependsOn("libfdx_desktop_c_opengl_build_debug")
}

tasks.register("lua_fdx_desktop_gl_c_run") {
    group = "samples"
    description = "Runs the jLua libFDX OpenGL desktop TeaVM C sample."
    dependsOn("libfdx_desktop_c_opengl_run_debug")
}
