import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
//    `kotlin-dsl`
    kotlin("jvm") version "1.3.70"
    id("com.jfrog.artifactory") version "4.17.2"
    `maven-publish`
    id("signing")
}

val dependencyHandlingVersion =
    loadProperties(file("version.properties").absolutePath).getProperty("dependencyHandlingVersion")

group = "org.daiv.dependency"
version = dependencyHandlingVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.10")
    implementation("org.daiv.dependency:VersionPluginConfiguration:0.0.15")
}

java {
    withJavadocJar()
    withSourcesJar()
}

signing {
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                packaging = "jar"
                name.set("DependencyHandling")
                description.set("the dependencyHandling for the org.daiv components")
                url.set("https://github.com/henry1986/dependencyHandling")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/henry1986/dependency-versionPlugin/issues")
                }
                scm {
                    connection.set("scm:git:https://github.com/henry1986/dependencyHandling.git")
                    developerConnection.set("scm:git:https://github.com/henry1986/dependencyHandling.git")
                    url.set("https://github.com/henry1986/dependencyHandling")
                }
                developers {
                    developer {
                        id.set("henry86")
                        name.set("Martin Heinrich")
                        email.set("martin.heinrich.dresden@gmx.de")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "sonatypeRepository"
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials(PasswordCredentials::class)
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
//            invokeMethod("publications",  publishing.publications.names.toTypedArray())
            setProperty("publishPom", true)
            setProperty("publishArtifacts", true)
        })
    })
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

