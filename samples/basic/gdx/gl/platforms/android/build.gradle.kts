import java.io.File
import java.util.Properties

plugins {
    id("com.android.application")
}

group = "lua.sample.gdx.gl.android"

val gdxNativeClassifiers = linkedMapOf(
    "armeabi-v7a" to "natives-armeabi-v7a",
    "arm64-v8a" to "natives-arm64-v8a",
    "x86" to "natives-x86",
    "x86_64" to "natives-x86_64"
)
val gdxNativeConfigurations = gdxNativeClassifiers.keys.associateWith { abi ->
    configurations.create("gdxNatives${abi.replace("-", "").replace("_", "")}") {
        isCanBeConsumed = false
        isCanBeResolved = true
    }
}
val stagedGdxJniLibsDir = layout.buildDirectory.dir("generated/gdxJniLibs")

val stageGdxJniLibs by tasks.registering(Copy::class) {
    gdxNativeConfigurations.forEach { (abi, configuration) ->
        from(configuration.incoming.artifactView { }.files.elements.map { files ->
            files.map { zipTree(it.asFile) }
        }) {
            include("*.so")
            into(abi)
        }
    }
    into(stagedGdxJniLibsDir)
    doFirst {
        delete(stagedGdxJniLibsDir)
    }
    doLast {
        val missing = gdxNativeClassifiers.keys.filter { abi ->
            !stagedGdxJniLibsDir.get().file("$abi/libgdx.so").asFile.isFile
        }
        if(missing.isNotEmpty()) {
            throw GradleException("Missing libGDX Android native libraries for ABI(s): ${missing.joinToString()}")
        }
    }
}

dependencies {
    implementation(project(":samples:basic:gdx:gl:core"))
    implementation(project(":lua:android:jni"))
    implementation("com.badlogicgames.gdx:gdx-backend-android:${LibExt.gdxVersion}")

    gdxNativeClassifiers.forEach { (abi, classifier) ->
        add(
            gdxNativeConfigurations.getValue(abi).name,
            "com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:$classifier"
        )
    }
}

android {
    namespace = "lua.sample.gdx.gl.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "lua.sample.gdx.gl.android"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    sourceSets {
        named("main") {
            jniLibs.srcDirs(stagedGdxJniLibsDir)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(LibExt.java8Target)
        targetCompatibility = JavaVersion.toVersion(LibExt.java8Target)
    }
}

tasks.matching { task ->
    task.name == "mergeDebugJniLibFolders" || task.name == "mergeReleaseJniLibFolders"
}.configureEach {
    dependsOn(stageGdxJniLibs)
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

tasks.register("lua_gdx_android_jni_build") {
    group = "samples"
    description = "Builds the jLua libGDX OpenGL Android JNI sample."
    dependsOn("assembleDebug")
}

tasks.register<Exec>("lua_gdx_android_jni_run") {
    group = "samples"
    description = "Installs and launches the jLua libGDX OpenGL Android JNI sample."
    dependsOn("installDebug")
    val command = mutableListOf(
        adbExecutable(),
        "shell",
        "am",
        "start",
        "-n",
        "lua.sample.gdx.gl.android/.LuaGdxAndroidActivity"
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
