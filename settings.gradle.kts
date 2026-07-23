pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
        maven {
            url = uri("http://teavm.org/maven/repository/")
            isAllowInsecureProtocol = true
        }
    }
}

rootProject.name = "jLua"

// Core
include(":lua:builder")
include(":lua:download")
include(":lua:base")
include(":lua:core")
include(":lua:shared:jni")
include(":lua:shared:c")
include(":lua:desktop:jni")
include(":lua:desktop:ffm")
include(":lua:desktop:c")
include(":lua:web:wasm")
include(":lua:android:jni")

// Extensions
include(":extensions:lua-ext")

// Samples
include(":samples:basic:shared")
include(":samples:basic:fdx:core")
include(":samples:basic:fdx:platforms:desktop-jni")
include(":samples:basic:fdx:platforms:desktop-ffm")
include(":samples:basic:fdx:platforms:desktop-c")
include(":samples:basic:fdx:platforms:web")
include(":samples:basic:fdx:platforms:android")
include(":samples:basic:gdx:gl:core")
include(":samples:basic:gdx:gl:platforms:desktop-jni")
include(":samples:basic:gdx:gl:platforms:desktop-ffm")
include(":samples:basic:gdx:gl:platforms:desktop-c")
include(":samples:basic:gdx:gl:platforms:web")
include(":samples:basic:gdx:gl:platforms:android")

// #### Use include build to use other project source directly. Just update the source path ####

//includeBuild("E:\\Dev\\Projects\\java\\jParser") {
//    dependencySubstitution {
//        substitute(module("com.github.xpenatan.jParser:gen-build")).using(project(":jParser:gen:gen-build"))
//        substitute(module("com.github.xpenatan.jParser:gen-build-tool")).using(project(":jParser:gen:gen-build-tool"))
//        substitute(module("com.github.xpenatan.jParser:gen-core")).using(project(":jParser:gen:gen-core"))
//        substitute(module("com.github.xpenatan.jParser:gen-idl")).using(project(":jParser:gen:gen-idl"))
//        substitute(module("com.github.xpenatan.jParser:gen-c")).using(project(":jParser:gen:gen-c"))
//        substitute(module("com.github.xpenatan.jParser:gen-ffm")).using(project(":jParser:gen:gen-ffm"))
//        substitute(module("com.github.xpenatan.jParser:gen-jni")).using(project(":jParser:gen:gen-jni"))
//        substitute(module("com.github.xpenatan.jParser:gen-web")).using(project(":jParser:gen:gen-web"))
//        substitute(module("com.github.xpenatan.jParser:api-core")).using(project(":jParser:api:api-core"))
//        substitute(module("com.github.xpenatan.jParser:api-web")).using(project(":jParser:api:api-web"))
//        substitute(module("com.github.xpenatan.jParser:runtime-base")).using(project(":jParser:runtime:base"))
//        substitute(module("com.github.xpenatan.jParser:runtime-core")).using(project(":jParser:runtime:core"))
//        substitute(module("com.github.xpenatan.jParser:runtime-android")).using(project(":jParser:runtime:android:runtime-android"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-ffm")).using(project(":jParser:runtime:desktop:runtime-desktop-ffm"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-ffm_windows_x64")).using(project(":jParser:runtime:desktop:runtime-desktop-ffm"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-ffm_linux_x64")).using(project(":jParser:runtime:desktop:runtime-desktop-ffm"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-ffm_mac_x64")).using(project(":jParser:runtime:desktop:runtime-desktop-ffm"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-ffm_mac_arm64")).using(project(":jParser:runtime:desktop:runtime-desktop-ffm"))
//        substitute(module("com.github.xpenatan.jParser:runtime-c")).using(project(":jParser:runtime:shared:runtime-c"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-c_windows_x64")).using(project(":jParser:runtime:desktop:runtime-desktop-c"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-c_linux_x64")).using(project(":jParser:runtime:desktop:runtime-desktop-c"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-c_mac_x64")).using(project(":jParser:runtime:desktop:runtime-desktop-c"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-c_mac_arm64")).using(project(":jParser:runtime:desktop:runtime-desktop-c"))
//        substitute(module("com.github.xpenatan.jParser:runtime-jni")).using(project(":jParser:runtime:shared:runtime-jni"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-jni_windows_x64")).using(project(":jParser:runtime:desktop:runtime-desktop-jni"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-jni_linux_x64")).using(project(":jParser:runtime:desktop:runtime-desktop-jni"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-jni_mac_x64")).using(project(":jParser:runtime:desktop:runtime-desktop-jni"))
//        substitute(module("com.github.xpenatan.jParser:runtime-desktop-jni_mac_arm64")).using(project(":jParser:runtime:desktop:runtime-desktop-jni"))
//        substitute(module("com.github.xpenatan.jParser:runtime-web")).using(project(":jParser:runtime:web:runtime-web"))
//        substitute(module("com.github.xpenatan.jParser:runtime-web_wasm")).using(project(":jParser:runtime:web:runtime-web"))
//        substitute(module("com.github.xpenatan.jParser:loader-core")).using(project(":jParser:loader:loader-core"))
//        substitute(module("com.github.xpenatan.jParser:loader-c")).using(project(":jParser:loader:loader-c"))
//        substitute(module("com.github.xpenatan.jParser:loader-web")).using(project(":jParser:loader:loader-web"))
//    }
//}
