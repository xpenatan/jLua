plugins {
    id("java")
    alias(libs.plugins.libfdx)
}

dependencies {
    implementation(project(":samples:basic:fdx:core"))
    implementation(project(":lua:web:wasm"))
    implementation(libs.libfdxBackendWeb)
    implementation(libs.libfdxGlWeb)
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

libfdx {
    js {
        mainClass.set("lua.sample.fdx.web.LuaFdxWebJsLauncher")
        htmlTitle.set("jLua Basic - libFDX WebGL JS")
        canvasId.set("libfdx-canvas")
        htmlWidth.set(0)
        htmlHeight.set(0)
        serverPort.set(8081)
    }
    wasm {
        mainClass.set("lua.sample.fdx.web.LuaFdxWebWasmLauncher")
        htmlTitle.set("jLua Basic - libFDX WebGL Wasm")
        canvasId.set("libfdx-canvas")
        htmlWidth.set(0)
        htmlHeight.set(0)
        serverPort.set(8082)
    }
}

val jsWebappDir = layout.buildDirectory.dir("dist/web-js/webapp")
val wasmWebappDir = layout.buildDirectory.dir("dist/web-wasm/webapp")

fun registerLuaRuntimeScriptCopy(
    taskName: String,
    prepareTaskName: String,
    webappDir: Provider<Directory>
): TaskProvider<Task> {
    val runtimeClasspath = configurations.named("runtimeClasspath")
    return tasks.register(taskName) {
        dependsOn(prepareTaskName, ":lua:web:wasm:jar")
        inputs.files(runtimeClasspath)
        outputs.file(webappDir.map { it.file("scripts/lua.js") })
        outputs.file(webappDir.map { it.file("scripts/lua.wasm") })
        doLast {
            val scriptsDir = webappDir.get().dir("scripts").asFile
            val scriptNames = setOf("lua.js", "lua.wasm")
            project.delete(scriptNames.map { File(scriptsDir, it) })
            project.copy {
                runtimeClasspath.get().files.forEach { entry ->
                    when {
                        entry.isDirectory -> from(entry) {
                            include("lua.js", "lua.wasm")
                        }
                        entry.isFile && entry.extension == "jar" -> from(zipTree(entry)) {
                            include("**/lua.js", "**/lua.wasm")
                            eachFile {
                                relativePath = RelativePath(true, name)
                            }
                            includeEmptyDirs = false
                        }
                    }
                }
                into(scriptsDir)
            }
            val missing = scriptNames.filterNot { File(scriptsDir, it).isFile }
            if(missing.isNotEmpty()) {
                throw GradleException(
                    "Missing jLua web runtime scripts: ${missing.joinToString()}. " +
                        "Run :lua:builder:lua_build_project_web before building the web sample."
                )
            }
        }
    }
}

val copyLuaJsRuntimeScripts = registerLuaRuntimeScriptCopy(
    "copyLuaJsRuntimeScripts",
    "libfdx_web_js_prepare",
    jsWebappDir
)
val copyLuaWasmRuntimeScripts = registerLuaRuntimeScriptCopy(
    "copyLuaWasmRuntimeScripts",
    "libfdx_web_wasm_prepare",
    wasmWebappDir
)

tasks.register("lua_fdx_webgl_js_build") {
    group = "samples"
    description = "Builds the jLua libFDX WebGL JavaScript sample."
    dependsOn("libfdx_web_js_build", copyLuaJsRuntimeScripts)
}

tasks.register("lua_fdx_webgl_wasm_build") {
    group = "samples"
    description = "Builds the jLua libFDX WebGL Wasm sample."
    dependsOn("libfdx_web_wasm_build", copyLuaWasmRuntimeScripts)
}

tasks.register<io.github.libfdx.gradle.LibfdxRunWebTask>("lua_fdx_webgl_js_run") {
    group = "samples"
    description = "Builds and serves the jLua libFDX WebGL JavaScript sample."
    dependsOn("lua_fdx_webgl_js_build")
    webappDir.set(jsWebappDir)
    port.set(libfdx.js.serverPort)
    defaultPath.set("/")
}

tasks.register<io.github.libfdx.gradle.LibfdxRunWebTask>("lua_fdx_webgl_wasm_run") {
    group = "samples"
    description = "Builds and serves the jLua libFDX WebGL Wasm sample."
    dependsOn("lua_fdx_webgl_wasm_build")
    webappDir.set(wasmWebappDir)
    port.set(libfdx.wasm.serverPort)
    defaultPath.set("/")
}
