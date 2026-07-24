import org.gradle.api.file.RelativePath
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
    id("java")
}

val luaSourceRoot = layout.buildDirectory.dir("lua-source")
val luaArchiveFile = layout.buildDirectory.file("tmp/lua-source.zip")
val luaVersion = libs.versions.luaSource.get()

tasks.register("lua_download_source") {
    group = "lua"
    description = "Downloads Lua $luaVersion source into the build directory."
    inputs.property("luaVersion", luaVersion)
    outputs.dir(luaSourceRoot)
    onlyIf {
        !luaSourceRoot.get().file("lapi.c").asFile.isFile
    }

    doLast {
        val sourceRoot = luaSourceRoot.get().asFile
        val archiveFile = luaArchiveFile.get().asFile
        val url = "https://github.com/lua/lua/archive/refs/tags/v$luaVersion.zip"
        println("URL: $url")
        delete(sourceRoot)
        archiveFile.parentFile.mkdirs()
        URL(url).openStream().use { input ->
            Files.copy(input, archiveFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        copy {
            from(zipTree(archiveFile)) {
                eachFile {
                    val strippedSegments = relativePath.segments.drop(1)
                    if(strippedSegments.isEmpty()) {
                        exclude()
                    }
                    else {
                        relativePath = RelativePath(!isDirectory, *strippedSegments.toTypedArray())
                    }
                }
                includeEmptyDirs = false
            }
            into(sourceRoot)
        }
        delete(archiveFile)
    }
}
