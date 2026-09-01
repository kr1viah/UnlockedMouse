import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.0"
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

tasks.register("collectFile") {
    group = "build"
    mustRunAfter("build")

    doLast {
        copy {
            from(file("build/libs/${project.property("archives_base_name")}-${project.property("mod_version")}+${project.property("minecraft_version")}.jar"))
            into(rootProject.file("build/libs"))
        }
        copy {
            from(file("build/libs/${project.property("archives_base_name")}-${project.property("mod_version")}+${project.property("minecraft_version")}-sources.jar"))
            into(rootProject.file("build/libs"))
        }
    }
}

tasks.register("buildAndCollect") {
    group = "build"
    dependsOn(tasks.named("build"), tasks.named("collectFile"))
}


val targetJavaVersion = 25
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}



repositories {}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

}

tasks.processResources {
    val mcVer = project.property("minecraft_version").toString().replace("-snapshot-", "-alpha.").replace("-pre-", "-pre.")
    inputs.property("version", project.version)
    inputs.property("minecraft_version", mcVer)
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version.toString(),
            "minecraft_version" to mcVer,
            "loader_version" to project.property("loader_version").toString()
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    repositories {}
}
