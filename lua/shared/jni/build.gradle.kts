plugins {
    id("java-library")
}

val moduleName = "lua-jni"
val luaGenerationTask = ":lua:builder:lua_build_project"

base {
    archivesName.set(moduleName)
}

dependencies {
    api(libs.bundles.jParserSharedJni)
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main/java"))
    }
}

tasks.named("compileJava") {
    dependsOn(luaGenerationTask)
}

tasks.named("javadoc") {
    dependsOn(luaGenerationTask)
}

tasks.matching { it.name == "sourcesJar" }.configureEach {
    dependsOn(luaGenerationTask)
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java"
        project.delete(files(srcPath))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
