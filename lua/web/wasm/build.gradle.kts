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
    api(libs.jParserRuntimeCore)
    api(libs.jParserRuntimeWeb)
    api(libs.jParserRuntimeWebWasm)
    compileOnlyApi(libs.jParserLoaderCore)
    api(libs.jParserLoaderWeb)
    implementation(libs.jParserApiCore)
    implementation(libs.jParserApiWeb)
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
