plugins {
    id("java")
    id("maven-publish")
    id("signing")
    id("io.github.libfdx") apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }

    val kotlinVersion = "2.2.21"

    dependencies {
        classpath("com.android.tools.build:gradle:8.12.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects  {

    repositories {
        mavenLocal {
            content {
                excludeGroup("com.github.xpenatan.jParser")
            }
        }
        google()
        mavenCentral()
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("http://teavm.org/maven/repository/")
            isAllowInsecureProtocol = true
        }
    }

    configurations.configureEach {
        // Check for updates every sync
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
        resolutionStrategy.eachDependency {
            if(requested.group == "com.github.xpenatan.jParser") {
                useVersion(LibExt.jParserVersion)
            }
            else if(requested.group == "com.github.xpenatan.gdx-teavm") {
                useVersion(LibExt.gdxTeaVMVersion)
            }
        }
    }
}

apply(plugin = "publish")
