plugins {
    id("java")
    id("io.github.libfdx")
}

dependencies {
    implementation(project(":samples:basic:core"))
    implementation(project(":lua:lua-web"))

    implementation("io.github.libfdx:backend_web:${LibExt.libfdxVersion}")
    implementation("io.github.libfdx:gl_web:${LibExt.libfdxVersion}")
}

libfdx {
    js {
        mainClass.set("lua.example.basic.LuaBasicWebJsLauncher")
        htmlTitle.set("xLua Basic - WebGL JS")
        canvasId.set("libfdx-canvas")
        htmlWidth.set(0)
        htmlHeight.set(0)
    }
    wasm {
        mainClass.set("lua.example.basic.LuaBasicWebWasmLauncher")
        htmlTitle.set("xLua Basic - WebGL Wasm")
        canvasId.set("libfdx-canvas")
        htmlWidth.set(0)
        htmlHeight.set(0)
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java25Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java25Target)
}

val jsWebappDir = layout.buildDirectory.dir("dist/web-js/webapp")
val wasmWebappDir = layout.buildDirectory.dir("dist/web-wasm/webapp")

tasks.register("lua_basic_webgl_js_build") {
    group = "application"
    description = "Builds the WebGL JavaScript Lua sample web application."
    dependsOn("libfdx_web_js_build")
}

tasks.register("lua_basic_webgl_wasm_build") {
    group = "application"
    description = "Builds the WebGL Wasm Lua sample web application."
    dependsOn("libfdx_web_wasm_build")
}

tasks.register<io.github.libfdx.gradle.LibfdxRunWebTask>("lua_basic_webgl_js_run") {
    group = "application"
    description = "Builds and serves the WebGL JavaScript Lua sample web application."
    dependsOn("lua_basic_webgl_js_build")
    webappDir.set(jsWebappDir)
    port.set(libfdx.js.serverPort)
    defaultPath.set("/")
}

tasks.register<io.github.libfdx.gradle.LibfdxRunWebTask>("lua_basic_webgl_wasm_run") {
    group = "application"
    description = "Builds and serves the WebGL Wasm Lua sample web application."
    dependsOn("lua_basic_webgl_wasm_build")
    webappDir.set(wasmWebappDir)
    port.set(libfdx.wasm.serverPort)
    defaultPath.set("/")
}
