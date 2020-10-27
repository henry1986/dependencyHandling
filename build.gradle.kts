plugins {
    `kotlin-dsl`
    id("com.jfrog.artifactory") version "4.17.2"
    `maven-publish`
}
group = "org.daiv.dependency"
version = "0.0.14"

repositories {
    mavenCentral()
}
dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(gradleApi())
    implementation(localGroovy())
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
            invokeMethod("publications", publishing.publications.names.toTypedArray())
            setProperty("publishPom", true)
            setProperty("publishArtifacts", true)
        })
    })
}
