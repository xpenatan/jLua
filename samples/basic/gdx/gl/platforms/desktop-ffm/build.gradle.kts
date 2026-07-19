import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
}

val luaRuntimeProject = ":lua:desktop:ffm"
val luaRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":samples:basic:gdx:gl:core"))
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    luaRuntimeClasspath(project(luaRuntimeProject))
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java8Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java8Target)
}

val sampleMainClass = "lua.sample.gdx.gl.desktop.LuaGdxDesktopLauncher"

tasks.register("lua_gdx_desktop_ffm_build") {
    group = "samples"
    description = "Builds the jLua libGDX OpenGL desktop FFM sample."
    dependsOn("classes", "$luaRuntimeProject:jar")
    inputs.files(luaRuntimeClasspath)
}

tasks.register<JavaExec>("lua_gdx_desktop_ffm_run") {
    group = "samples"
    description = "Runs the jLua libGDX OpenGL desktop FFM sample."
    dependsOn("lua_gdx_desktop_ffm_build")
    mainClass.set(sampleMainClass)
    classpath = luaRuntimeClasspath + sourceSets["main"].runtimeClasspath
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(LibExt.java25Target.toInt()))
    })
    systemProperty("jlua.sample.backend", "libGDX OpenGL FFM")
    forwardSampleProperty("jlua.sample.exitAfterFrames")
    forwardSampleProperty("jlua.sample.failFast")
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    if(DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

fun JavaExec.forwardSampleProperty(name: String) {
    val value = providers.gradleProperty(name).orNull ?: System.getProperty(name)
    value?.takeIf { it.isNotBlank() }?.let { systemProperty(name, it) }
}
