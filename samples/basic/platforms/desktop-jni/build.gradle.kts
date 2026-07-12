import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
}

val glRuntimeClasspath by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(project(":samples:basic:core"))
    implementation(project(":lua:desktop:jni"))

    implementation("io.github.libfdx:backend_desktop:${LibExt.libfdxVersion}")
    implementation("io.github.libfdx:display:${LibExt.libfdxVersion}")
    glRuntimeClasspath("io.github.libfdx:gl_desktop:${LibExt.libfdxVersion}")
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java25Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java25Target)
}

val mainClassName = "lua.example.basic.Main"
val assetsDir = File("../../assets")

tasks.register<JavaExec>("lua_basic_desktop_jni_run") {
    group = "application"
    description = "Runs the libFDX desktop Lua sample with GL using JNI."
    dependsOn("classes")
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath + glRuntimeClasspath
    workingDir = assetsDir
    systemProperty("jlua.sample.backend", "JNI")
    forwardSampleProperty("jlua.sample.exitAfterFrames")
    forwardSampleProperty("jlua.sample.failFast")
    jvmArgs("-Dorg.lwjgl.system.stackSize=1048576")
    jvmArgs("--enable-native-access=ALL-UNNAMED")

    if(DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

fun JavaExec.forwardSampleProperty(name: String) {
    providers.gradleProperty(name).orNull?.let { value ->
        systemProperty(name, value)
    }
}
