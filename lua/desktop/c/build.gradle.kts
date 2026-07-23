import org.gradle.api.GradleException
import java.util.Locale

plugins {
    id("base")
}

val moduleName = "lua-desktop-c"
val libDir = "${projectDir}/../../builder/build/c++/libs"
val nativeResourceRoot = "external_cpp/jparser/lua/native"
val gdxTeaVMMarker = resources.text.fromString("ignore-resources=META-INF\n")

data class PlatformSpec(
    val buildTask: String,
    val nativeFiles: List<String>,
)

val platforms = mapOf(
    "windows_x64" to PlatformSpec(
        ":lua:builder:lua_build_project_windows64_teavm_c",
        listOf(
            "$libDir/windows/vc/teavm_c/lua64_.lib",
            "$libDir/windows/vc/teavm_c/lua64.lib",
            "$libDir/windows/vc/teavm_c/lua64.dll",
        ),
    ),
    "linux_x64" to PlatformSpec(
        ":lua:builder:lua_build_project_linux64_teavm_c",
        listOf(
            "$libDir/linux/teavm_c/liblua64_.a",
            "$libDir/linux/teavm_c/liblua64.so",
        ),
    ),
    "mac_x64" to PlatformSpec(
        ":lua:builder:lua_build_project_mac64_teavm_c",
        listOf(
            "$libDir/mac/teavm_c/liblua64_.a",
            "$libDir/mac/teavm_c/liblua64.dylib",
        ),
    ),
    "mac_arm64" to PlatformSpec(
        ":lua:builder:lua_build_project_macArm_teavm_c",
        listOf(
            "$libDir/mac/arm/teavm_c/liblua64_.a",
            "$libDir/mac/arm/teavm_c/libluaarm64.dylib",
        ),
    ),
)

val jParserDesktopCRuntimes = mapOf(
    "windows_x64" to libs.jParserRuntimeDesktopCWindowsX64,
    "linux_x64" to libs.jParserRuntimeDesktopCLinuxX64,
    "mac_x64" to libs.jParserRuntimeDesktopCMacX64,
    "mac_arm64" to libs.jParserRuntimeDesktopCMacArm64,
)

fun currentDesktopPlatform(): String {
    val os = System.getProperty("os.name").lowercase(Locale.ROOT)
    val arch = System.getProperty("os.arch").lowercase(Locale.ROOT)
    return when {
        os.contains("windows") && (arch == "amd64" || arch == "x86_64" || arch == "x86-64") -> "windows_x64"
        os.contains("linux") && (arch == "amd64" || arch == "x86_64" || arch == "x86-64") -> "linux_x64"
        os.contains("mac") && (arch == "aarch64" || arch == "arm64") -> "mac_arm64"
        os.contains("mac") && (arch == "amd64" || arch == "x86_64" || arch == "x86-64") -> "mac_x64"
        else -> throw GradleException("Unsupported desktop TeaVM C host: os=$os arch=$arch")
    }
}

val nativeJars = platforms.mapValues { (platform, spec) ->
    tasks.register<Jar>("nativeJar_$platform") {
        spec.nativeFiles.forEach { nativeFile ->
            from(nativeFile) {
                into("$nativeResourceRoot/$platform")
            }
        }
        from(gdxTeaVMMarker.asFile()) {
            into("META-INF")
            rename { "gdx-teavm.properties" }
        }
        archiveBaseName.set("$moduleName-$platform")
        archiveClassifier.set("")
        doFirst {
            val missingFiles = spec.nativeFiles.filterNot { file(it).isFile }
            if(missingFiles.isNotEmpty()) {
                throw GradleException(
                    "Missing jLua TeaVM C native payloads for $platform:\n" +
                        missingFiles.joinToString("\n")
                )
            }
        }
    }
}

val nativeRuntime by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

val platformNativeRuntimes = platforms.keys.associateWith { platform ->
    configurations.create("nativeRuntime_$platform") {
        isCanBeConsumed = true
        isCanBeResolved = false
    }
}

artifacts {
    nativeJars.forEach { (platform, nativeJar) ->
        add(nativeRuntime.name, nativeJar)
        add(platformNativeRuntimes.getValue(platform).name, nativeJar)
    }
}

val hostPlatform = currentDesktopPlatform()
nativeJars.getValue(hostPlatform).configure {
    dependsOn(platforms.getValue(hostPlatform).buildTask)
}

publishing {
    publications {
        platforms.keys.forEach { platform ->
            create<MavenPublication>("mavenNative_$platform") {
                artifactId = "${moduleName}_$platform"
                artifact(nativeJars.getValue(platform))
                pom.withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")

                    val luaCDependency = dependenciesNode.appendNode("dependency")
                    luaCDependency.appendNode("groupId", project.group.toString())
                    luaCDependency.appendNode("artifactId", "lua-c")
                    luaCDependency.appendNode("version", project.version.toString())
                    luaCDependency.appendNode("scope", "compile")

                    val jParserRuntime = jParserDesktopCRuntimes.getValue(platform).get()
                    val jParserRuntimeDependency = dependenciesNode.appendNode("dependency")
                    jParserRuntimeDependency.appendNode("groupId", jParserRuntime.module.group)
                    jParserRuntimeDependency.appendNode("artifactId", jParserRuntime.module.name)
                    jParserRuntimeDependency.appendNode("version", jParserRuntime.versionConstraint.requiredVersion)
                    jParserRuntimeDependency.appendNode("scope", "compile")
                }
            }
        }
    }
}
