import java.io.File
import java.util.*

object LibExt {
    const val groupId = "com.github.xpenatan.xLua"
    const val libName = "xLua"
    var isRelease = false
    var libVersion: String = ""
        get() {
            return getVersion()
        }

    const val java8Target = "1.8"
    const val java17Target = "17"
    const val java25Target = "25"

    const val jParserVersion = "-SNAPSHOT"
    const val teaVMVersion = "0.15.0"
    const val libfdxVersion = "-SNAPSHOT"
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
