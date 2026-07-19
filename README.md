# jLua

WIP Lua bindings for Java applications on desktop, web, and Android.

TeaVM C bindings and native libraries can be generated for each supported desktop target with:

```powershell
.\gradlew.bat :lua:builder:lua_build_project_windows64_teavm_c
.\gradlew.bat :lua:builder:lua_build_project_linux64_teavm_c
.\gradlew.bat :lua:builder:lua_build_project_mac64_teavm_c
.\gradlew.bat :lua:builder:lua_build_project_macArm_teavm_c
```

## Samples

The basic sample lives under `samples/basic`, with its shared Lua implementation and separate `fdx` and `gdx` backend branches kept inside that folder. Both integrations use OpenGL/OpenGL ES/WebGL; no WebGPU sample is included.

Run the desktop JNI and FFM samples for 60 frames with:

```powershell
.\gradlew.bat "--project-prop=jlua.sample.exitAfterFrames=60" "--project-prop=jlua.sample.failFast=true" :samples:basic:fdx:platforms:desktop-jni:lua_fdx_desktop_gl_jni_run
.\gradlew.bat "--project-prop=jlua.sample.exitAfterFrames=60" "--project-prop=jlua.sample.failFast=true" :samples:basic:fdx:platforms:desktop-ffm:lua_fdx_desktop_gl_ffm_run
.\gradlew.bat "--project-prop=jlua.sample.exitAfterFrames=60" "--project-prop=jlua.sample.failFast=true" :samples:basic:gdx:gl:platforms:desktop-jni:lua_gdx_desktop_jni_run
.\gradlew.bat "--project-prop=jlua.sample.exitAfterFrames=60" "--project-prop=jlua.sample.failFast=true" :samples:basic:gdx:gl:platforms:desktop-ffm:lua_gdx_desktop_ffm_run
```

The remaining OpenGL targets are available through:

```powershell
.\gradlew.bat :samples:basic:fdx:platforms:desktop-c:lua_fdx_desktop_gl_c_run
.\gradlew.bat :samples:basic:fdx:platforms:web:lua_fdx_webgl_js_run
.\gradlew.bat :samples:basic:fdx:platforms:web:lua_fdx_webgl_wasm_run
.\gradlew.bat :samples:basic:fdx:platforms:android:lua_fdx_android_gles_run

.\gradlew.bat :samples:basic:gdx:gl:platforms:desktop-c:lua_gdx_desktop_c_run
.\gradlew.bat :samples:basic:gdx:gl:platforms:web:lua_gdx_webgl_js_run
.\gradlew.bat :samples:basic:gdx:gl:platforms:web:lua_gdx_webgl_wasm_run
.\gradlew.bat :samples:basic:gdx:gl:platforms:android:lua_gdx_android_jni_run
```
