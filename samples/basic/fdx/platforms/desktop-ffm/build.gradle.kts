import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
}

val glRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":samples:basic:fdx:core"))
    implementation(project(":lua:desktop:ffm"))
    implementation("io.github.libfdx:backend_desktop:${LibExt.libfdxVersion}")
    implementation("io.github.libfdx:display:${LibExt.libfdxVersion}")
    glRuntimeClasspath("io.github.libfdx:gl_desktop:${LibExt.libfdxVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java25Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java25Target)
}

val sampleMainClass = "lua.sample.fdx.desktop.LuaFdxDesktopLauncher"

tasks.register("lua_fdx_desktop_gl_ffm_build") {
    group = "samples"
    description = "Builds the jLua libFDX OpenGL desktop FFM sample."
    dependsOn("classes")
    inputs.files(glRuntimeClasspath)
}

tasks.register<JavaExec>("lua_fdx_desktop_gl_ffm_run") {
    group = "samples"
    description = "Runs the jLua libFDX OpenGL desktop FFM sample."
    dependsOn("lua_fdx_desktop_gl_ffm_build")
    mainClass.set(sampleMainClass)
    classpath = sourceSets["main"].runtimeClasspath + glRuntimeClasspath
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(LibExt.java25Target.toInt()))
    })
    systemProperty("jlua.sample.backend", "libFDX OpenGL FFM")
    forwardSampleProperty("jlua.sample.exitAfterFrames")
    forwardSampleProperty("jlua.sample.failFast")
    jvmArgs("-Dorg.lwjgl.system.stackSize=1048576")
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    if(DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

fun JavaExec.forwardSampleProperty(name: String) {
    val value = providers.gradleProperty(name).orNull ?: System.getProperty(name)
    value?.takeIf { it.isNotBlank() }?.let { systemProperty(name, it) }
}
