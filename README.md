# jLua

WIP Lua bindings for libFDX applications.

TeaVM C bindings and native libraries can be generated for each supported desktop target with:

```powershell
.\gradlew.bat :lua:builder:lua_build_project_windows64_teavm_c
.\gradlew.bat :lua:builder:lua_build_project_linux64_teavm_c
.\gradlew.bat :lua:builder:lua_build_project_mac64_teavm_c
.\gradlew.bat :lua:builder:lua_build_project_macArm_teavm_c
```

Build and run the sample on the current host for 60 frames with:

```powershell
.\gradlew.bat "--project-prop=libfdx.desktopC.runArgs=60" "--project-prop=libfdx.desktopC.openConsole=false" :samples:basic:platforms:desktop-c:libfdx_desktop_c_opengl_run_debug
```
