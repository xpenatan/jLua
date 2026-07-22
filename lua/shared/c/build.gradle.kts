plugins {
    id("java-library")
}

val moduleName = "lua-c"
val luaGenerationTask = ":lua:builder:lua_build_project"

base {
    archivesName.set(moduleName)
}

dependencies {
    api(project(":lua:core"))
    api("com.github.xpenatan.jParser:runtime-c:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:api-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:loader-c:${LibExt.jParserVersion}")
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main/java"))
        java.include("gen/c/**/*.java")
        resources.setSrcDirs(listOf("src/main/resources", "build/generated/jparser/resources/main"))
    }
}

tasks.named("compileJava") {
    dependsOn(luaGenerationTask)
}

tasks.named("processResources") {
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
        project.delete(files("$projectDir/src/main/java"))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java17Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java17Target)
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
