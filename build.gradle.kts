import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
//    `kotlin-dsl`
    kotlin("jvm") version "1.3.70"
    id("com.jfrog.artifactory") version "4.17.2"
    `maven-publish`
}

val dependencyHandlingVersion = loadProperties(file("version.properties").absolutePath).getProperty("dependencyHandlingVersion")

group = "org.daiv.dependency"
version = dependencyHandlingVersion


repositories {
    mavenCentral()
    maven("https://daiv.org/artifactory/gradle-dev-local")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("org.daiv.dependency:VersionPluginConfiguration:0.0.10")
//    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.70")
//    implementation(kotlin("gradle-plugin"))
//    implementation(kotlin("reflect", "1.4.10"))
//    implementation(gradleApi())
}
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

artifactory {
    setContextUrl("${project.findProperty("daiv_contextUrl")}")
    publish(delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig> {
        repository(delegateClosureOf<groovy.lang.GroovyObject> {
            setProperty("repoKey", "gradle-dev-local")
            setProperty("username", project.findProperty("daiv_user"))
            setProperty("password", project.findProperty("daiv_password"))
            setProperty("maven", true)
        })

        defaults(delegateClosureOf<groovy.lang.GroovyObject> {
            invokeMethod("publications",  arrayOf("mavenJava"))
//            invokeMethod("publications",  publishing.publications.names.toTypedArray())
            setProperty("publishPom", true)
            setProperty("publishArtifacts", true)
        })
    })
}

