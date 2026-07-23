plugins {
    id("java")
    alias(libs.plugins.easyPublishing)
}

val jLuaGroup = "com.github.xpenatan.jLua"

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

    groupId.set(jLuaGroup)
    releaseVersion.set(libs.versions.jLuaRelease)
    snapshotVersion.set(libs.versions.jLuaSnapshot)

    snapshotRepositoryUrl.set("https://central.sonatype.com/repository/maven-snapshots/")
    releaseRepositoryUrl.set("https://central.sonatype.com")
    username.set(providers.environmentVariable("CENTRAL_PORTAL_USERNAME"))
    password.set(providers.environmentVariable("CENTRAL_PORTAL_PASSWORD"))
    signingKey.set(providers.environmentVariable("SIGNING_KEY"))
    signingPassword.set(providers.environmentVariable("SIGNING_PASSWORD"))

    pomName.set("jLua")
    pomDescription.set("Lua Java Bindings")
    projectUrl.set("https://github.com/xpenatan/jLua")

    developerId.set("Xpe")
    developerName.set("Natan")

    scmUrl.set("https://github.com/xpenatan/jLua")
    scmConnection.set("scm:git:https://github.com/xpenatan/jLua.git")
    scmDeveloperConnection.set("scm:git:ssh://git@github.com/xpenatan/jLua.git")
}
