plugins {
    id("com.android.library")
}

val moduleName = "lua-android"
group = "${LibExt.groupId}.android"

android {
    namespace = "lua"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    sourceSets {
        named("main") {
            jniLibs.srcDirs("$projectDir/../../builder/build/c++/libs/android")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(LibExt.java8Target)
        targetCompatibility = JavaVersion.toVersion(LibExt.java8Target)
    }
    publishing {
        singleVariant("release")
    }
}

dependencies {
    api(project(":lua:shared:jni"))
    api("com.github.xpenatan.jParser:api-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-jni:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-android:${LibExt.jParserVersion}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            groupId = LibExt.groupId
            version = LibExt.libVersion
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
