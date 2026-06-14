plugins {
    id("java-library")
}

val moduleName = "lua-web"

val emscriptenJS = "$projectDir/../lua-build/build/c++/libs/emscripten/lua.js"
val emscriptenWASM = "$projectDir/../lua-build/build/c++/libs/emscripten/lua.wasm"

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
    api("org.teavm:teavm-jso:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-core:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-classlib:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-jso-apis:${LibExt.teaVMVersion}")

    compileOnlyApi("com.github.xpenatan.jParser:loader-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:loader-web:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:api-core:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:api-web:${LibExt.jParserVersion}")
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java"
        val jsPath = "$projectDir/src/main/resources/lua.wasm.js"
        project.delete(files(srcPath, jsPath))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java11Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java11Target)
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            group = LibExt.groupId
            version = LibExt.libVersion
            from(components["java"])
        }
    }
}
