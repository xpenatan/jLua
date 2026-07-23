plugins {
    alias(libs.plugins.gdxTeaVM)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":samples:basic:gdx:gl:core"))
    implementation(project(":lua:web:wasm"))
}

gdxTeaVM {
    js {
        mainClass.set("lua.sample.gdx.gl.web.LuaGdxWebLauncher")
        htmlTitle.set("jLua Basic - libGDX WebGL JS")
        htmlWidth.set(0)
        htmlHeight.set(0)
        serverPort.set(8083)
        processMemory.set(2048)
        obfuscated.set(false)
    }
    wasm {
        mainClass.set("lua.sample.gdx.gl.web.LuaGdxWebLauncher")
        htmlTitle.set("jLua Basic - libGDX WebGL Wasm")
        htmlWidth.set(0)
        htmlHeight.set(0)
        serverPort.set(8084)
        processMemory.set(2048)
        obfuscated.set(false)
        strict.set(false)
    }
}

tasks.register("lua_gdx_webgl_js_build") {
    group = "samples"
    description = "Builds the jLua libGDX WebGL JavaScript sample."
    dependsOn("gdx_teavm_web_js_build")
}

tasks.register("lua_gdx_webgl_js_run") {
    group = "samples"
    description = "Builds and serves the jLua libGDX WebGL JavaScript sample."
    dependsOn("gdx_teavm_web_js_run")
}

tasks.register("lua_gdx_webgl_wasm_build") {
    group = "samples"
    description = "Builds the jLua libGDX WebGL Wasm sample."
    dependsOn("gdx_teavm_web_wasm_build")
}

tasks.register("lua_gdx_webgl_wasm_run") {
    group = "samples"
    description = "Builds and serves the jLua libGDX WebGL Wasm sample."
    dependsOn("gdx_teavm_web_wasm_run")
}
