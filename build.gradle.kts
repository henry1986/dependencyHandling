plugins {
//    `kotlin-dsl`
    kotlin("jvm") version "1.3.70"
    id("com.jfrog.artifactory") version "4.17.2"
    `maven-publish`
}

group = "org.daiv.dependency"
version = "0.0.21"


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
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
            invokeMethod("publications", arrayOf("mavenJava"))
            setProperty("publishPom", true)
            setProperty("publishArtifacts", true)
        })
    })
}
