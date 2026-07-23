plugins {
    alias(libs.plugins.androidLibrary)
}

val moduleName = "lua-android"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    publishing {
        singleVariant("release")
    }
}

dependencies {
    api(project(":lua:shared:jni"))
    api(libs.bundles.jParserAndroid)
}

tasks.named("preBuild") {
    dependsOn(":lua:builder:lua_build_project_android")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
