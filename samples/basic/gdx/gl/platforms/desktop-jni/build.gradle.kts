import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
}

val luaRuntimeProject = ":lua:desktop:jni"
val luaRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":samples:basic:gdx:gl:core"))
    implementation(libs.gdxBackendLwjgl3)
    implementation(variantOf(libs.gdxPlatform) { classifier("natives-desktop") })
    luaRuntimeClasspath(project(luaRuntimeProject))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val sampleMainClass = "lua.sample.gdx.gl.desktop.LuaGdxDesktopLauncher"

tasks.register("lua_gdx_desktop_jni_build") {
    group = "samples"
    description = "Builds the jLua libGDX OpenGL desktop JNI sample."
    dependsOn("classes", "$luaRuntimeProject:jar")
    inputs.files(luaRuntimeClasspath)
}

tasks.register<JavaExec>("lua_gdx_desktop_jni_run") {
    group = "samples"
    description = "Runs the jLua libGDX OpenGL desktop JNI sample."
    dependsOn("lua_gdx_desktop_jni_build")
    mainClass.set(sampleMainClass)
    classpath = luaRuntimeClasspath + sourceSets["main"].runtimeClasspath
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    systemProperty("jlua.sample.backend", "libGDX OpenGL JNI")
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
