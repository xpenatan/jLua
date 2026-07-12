plugins {
    id("java")
    id("io.github.libfdx")
}

dependencies {
    implementation(project(":samples:basic:core"))
    implementation(project(":lua:web:wasm"))

    implementation("io.github.libfdx:backend_web:${LibExt.libfdxVersion}")
    implementation("io.github.libfdx:gl_web:${LibExt.libfdxVersion}")
}

val luaBuildDir = project(":lua:builder").layout.buildDirectory
val luaWebRuntimeFiles = files(
    luaBuildDir.file("c++/libs/emscripten/lua.js"),
    luaBuildDir.file("c++/libs/emscripten/lua.wasm")
)

fun copyLuaWebRuntime(webTargetDir: String) {
    copy {
        from(luaWebRuntimeFiles)
        into(layout.buildDirectory.dir("dist/$webTargetDir/webapp/scripts"))
    }
}

tasks.matching { it.name == "libfdx_web_js_prepare" }.configureEach {
    dependsOn(":lua:builder:lua_build_project_web")
    doLast {
        copyLuaWebRuntime("web-js")
    }
}

tasks.matching { it.name == "libfdx_web_wasm_prepare" }.configureEach {
    dependsOn(":lua:builder:lua_build_project_web")
    doLast {
        copyLuaWebRuntime("web-wasm")
    }
}

libfdx {
    js {
        mainClass.set("lua.example.basic.LuaBasicWebJsLauncher")
        htmlTitle.set("jLua Basic - WebGL JS")
        canvasId.set("libfdx-canvas")
        htmlWidth.set(0)
        htmlHeight.set(0)
    }
    wasm {
        mainClass.set("lua.example.basic.LuaBasicWebWasmLauncher")
        htmlTitle.set("jLua Basic - WebGL Wasm")
        canvasId.set("libfdx-canvas")
        htmlWidth.set(0)
        htmlHeight.set(0)
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(LibExt.java25Target)
    targetCompatibility = JavaVersion.toVersion(LibExt.java25Target)
}
