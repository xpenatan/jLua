plugins {
    id("java")
    id("com.github.xpenatan.easy-publishing") version "-SNAPSHOT"
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

easyPublishing {
    modules(
        ":lua:core",
        ":lua:shared:jni",
        ":lua:shared:c",
        ":lua:desktop:jni",
        ":lua:desktop:ffm",
        ":lua:desktop:c",
        ":lua:web:wasm",
        ":lua:android:jni",
        ":extensions:lua-ext"
    )

    groupId.set(LibExt.groupId)
    releaseVersion.set(providers.gradleProperty("version"))
    snapshotVersion.set("-SNAPSHOT")

    snapshotRepositoryUrl.set("https://central.sonatype.com/repository/maven-snapshots/")
    releaseRepositoryUrl.set("https://central.sonatype.com")
    username.set(providers.environmentVariable("CENTRAL_PORTAL_USERNAME"))
    password.set(providers.environmentVariable("CENTRAL_PORTAL_PASSWORD"))
    signingKey.set(providers.environmentVariable("SIGNING_KEY"))
    signingPassword.set(providers.environmentVariable("SIGNING_PASSWORD"))

    pomName.set(LibExt.libName)
    pomDescription.set("Lua Java Bindings")
    projectUrl.set("https://github.com/xpenatan/jLua")

    developerId.set("Xpe")
    developerName.set("Natan")

    scmUrl.set("https://github.com/xpenatan/jLua")
    scmConnection.set("scm:git:https://github.com/xpenatan/jLua.git")
    scmDeveloperConnection.set("scm:git:ssh://git@github.com/xpenatan/jLua.git")
}
