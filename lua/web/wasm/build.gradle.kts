plugins {
    id("java-library")
}

val moduleName = "lua-web"
val luaGenerationTask = ":lua:builder:lua_build_project"

val emscriptenJS = "$projectDir/../../builder/build/c++/libs/emscripten/lua.js"
val emscriptenWASM = "$projectDir/../../builder/build/c++/libs/emscripten/lua.wasm"

val compileWebCompat by tasks.registering(JavaCompile::class) {
    source("src/webCompat/java")
    classpath = files()
    destinationDirectory.set(layout.buildDirectory.dir("generated/webCompat/classes"))
    options.release.set(8)
}

tasks.jar {
    dependsOn(compileWebCompat)
    from(compileWebCompat.map { it.destinationDirectory })
    from(emscriptenJS, emscriptenWASM)
}

dependencies {
    api("com.github.xpenatan.jParser:runtime-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-web:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:runtime-web_wasm:${LibExt.jParserVersion}")
    compileOnlyApi("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:loader-web:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:api-core:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:api-web:${LibExt.jParserVersion}")
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
        val srcPath = "$projectDir/src/main/java"
        val jsPath = "$projectDir/src/main/resources/lua.wasm.js"
        project.delete(files(srcPath, jsPath))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java17Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java17Target)
}

java {
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
