plugins {
    id("java-library")
}

val moduleName = "lua-jni"
group = "${LibExt.groupId}.shared"
val luaGenerationTask = ":lua:builder:lua_build_project"

base {
    archivesName.set(moduleName)
}

dependencies {
    api("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:api-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-jni:${LibExt.jParserVersion}")
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main/java"))
    }
}

tasks.named("compileJava") {
    dependsOn(luaGenerationTask)
}

tasks.named("javadoc") {
    dependsOn(luaGenerationTask)
}

tasks.matching { it.name == "sourcesJar" }.configureEach {
    dependsOn(luaGenerationTask)
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java"
        project.delete(files(srcPath))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java8Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java8Target)
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            groupId = LibExt.groupId
            version = LibExt.libVersion
            from(components["java"])
        }
    }
}
