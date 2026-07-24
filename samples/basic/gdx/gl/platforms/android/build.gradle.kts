import java.io.File
import java.util.Properties

abstract class StageGdxJniLibs : Copy() {
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty
}

plugins {
    alias(libs.plugins.androidApplication)
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
val stageGdxJniLibs = tasks.register<StageGdxJniLibs>("stageGdxJniLibs") {
    outputDirectory.convention(layout.buildDirectory.dir("generated/gdxJniLibs"))
    gdxNativeConfigurations.forEach { (abi, configuration) ->
        from(configuration.incoming.artifactView { }.files.elements.map { files ->
            files.map { zipTree(it.asFile) }
        }) {
            include("*.so")
            into(abi)
        }
    }
    into(outputDirectory)
    doFirst {
        delete(outputDirectory)
    }
    doLast {
        val missing = gdxNativeClassifiers.keys.filter { abi ->
            !outputDirectory.get().file("$abi/libgdx.so").asFile.isFile
        }
        if(missing.isNotEmpty()) {
            throw GradleException("Missing libGDX Android native libraries for ABI(s): ${missing.joinToString()}")
        }
    }
}

dependencies {
    implementation(project(":samples:basic:gdx:gl:core"))
    implementation(project(":lua:android:jni"))
    implementation(libs.gdxBackendAndroid)

    gdxNativeClassifiers.forEach { (abi, classifier) ->
        add(
            gdxNativeConfigurations.getValue(abi).name,
            variantOf(libs.gdxPlatform) { classifier(classifier) }
        )
    }
}

android {
    enableKotlin = false
    namespace = "lua.sample.gdx.gl.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "lua.sample.gdx.gl.android"
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

androidComponents {
    onVariants(selector().all()) { variant ->
        variant.sources.jniLibs?.addGeneratedSourceDirectory(
            stageGdxJniLibs,
            StageGdxJniLibs::outputDirectory
        )
    }
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
