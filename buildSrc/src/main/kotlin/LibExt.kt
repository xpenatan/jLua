import java.io.File
import java.util.*

object LibExt {
    const val groupId = "com.github.xpenatan.jLua"
    const val libName = "jLua"
    var isRelease = false
    var libVersion: String = ""
        get() {
            return getVersion()
        }

    const val java8Target = "1.8"
    const val java17Target = "17"
    const val java25Target = "25"

    const val luaVersion = "5.4.6"
    const val jParserVersion = "-SNAPSHOT"
    const val teaVMVersion = "0.15.0"
    const val libfdxVersion = "-SNAPSHOT"
    const val gdxVersion = "1.14.2"
    const val gdxTeaVMVersion = "1.6.0"
}

private fun getVersion(): String {
    var libVersion = "-SNAPSHOT"
    val file = File("gradle.properties")
    if(file.exists()) {
        val properties = Properties()
        properties.load(file.inputStream())
        val version = properties.getProperty("version")
        if(LibExt.isRelease) {
            libVersion = version
        }
    }
    else {
        if(LibExt.isRelease) {
            throw RuntimeException("properties should exist")
        }
    }
    return libVersion
}
