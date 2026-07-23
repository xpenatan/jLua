import java.io.File
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
}

group = "lua.sample.fdx.android"

android {
    namespace = "lua.sample.fdx.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "lua.sample.fdx.android"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibs)
    implementation(project(":samples:basic:fdx:core"))
    implementation(project(":lua:android:jni"))
    implementation(libs.libfdxBackendAndroid)
}

tasks.named("preBuild") {
    dependsOn(":lua:builder:lua_build_project_android")
}

fun adbExecutable(): String {
    val executable = if(System.getProperty("os.name").lowercase().contains("win")) "adb.exe" else "adb"
    val sdkRoots = mutableListOf<String>()
    val localPropertiesFile = rootProject.file("local.properties")
    if(localPropertiesFile.isFile) {
        val localProperties = Properties()
        localPropertiesFile.inputStream().use { localProperties.load(it) }
        localProperties.getProperty("sdk.dir")?.let { sdkRoots += it }
    }
    System.getenv("ANDROID_HOME")?.let { sdkRoots += it }
    System.getenv("ANDROID_SDK_ROOT")?.let { sdkRoots += it }
    sdkRoots.asSequence()
        .map { file("$it/platform-tools/$executable") }
        .firstOrNull { it.isFile }
        ?.let { return it.absolutePath }

    System.getenv("PATH").orEmpty().split(File.pathSeparator)
        .asSequence()
        .map { File(it, executable) }
        .firstOrNull { it.isFile }
        ?.let { return it.absolutePath }

    throw GradleException(
        "Could not find $executable. Set sdk.dir in local.properties, set ANDROID_HOME or ANDROID_SDK_ROOT, or add adb to PATH."
    )
}

tasks.register("lua_fdx_android_gles_build") {
    group = "samples"
    description = "Builds the jLua libFDX Android OpenGL ES sample."
    dependsOn("assembleDebug")
}

tasks.register<Exec>("lua_fdx_android_gles_run") {
    group = "samples"
    description = "Installs and launches the jLua libFDX Android OpenGL ES sample."
    dependsOn("installDebug")
    val command = mutableListOf(
        adbExecutable(),
        "shell",
        "am",
        "start",
        "-n",
        "lua.sample.fdx.android/.LuaFdxAndroidActivity"
    )
    System.getProperties().stringPropertyNames()
        .filter { it.startsWith("jlua.sample.") }
        .sorted()
        .forEach { key ->
            val value = System.getProperty(key)
            if(!value.isNullOrBlank()) {
                command.addAll(listOf("--es", key, value))
            }
        }
    commandLine(command)
}
