package org.daiv.dependency

import com.google.gson.GsonBuilder


data class Versions(
    val serialization: String,
    val kutil: String,
    val coroutines: String,
    val ktor: String,
    val jpersistence: String,
    val eventbus: String,
    val gson: String,
    val kotlinx_html: String,
    val mockk: String,
    val sqlite_jdbc: String,
    val postgres_jdbc: String
) {
    companion object {
        const val versionJsonPath = "org/daiv/dependency"
        fun current(json: String = Versions::class.java.classLoader.getResource("$versionJsonPath/versions.json").readText()): Versions {
            return GsonBuilder().setPrettyPrinting().create().fromJson(json, Versions::class.java)!!
        }

        fun versionPluginBuilder(configure: VersionPluginExtension<Versions>.() -> Unit) =
            VersionsPluginBuilder(versionJsonPath, Versions::class, configure)
                .extension
    }
}

interface StandardBuilder {
    val versions: Versions
    fun serialization() = "org.jetbrains.kotlinx:kotlinx-serialization-core:${versions.serialization}"
    fun kutil() = "org.daiv.util:kutil:${versions.kutil}"

    fun eventbus() = "org.daiv.websocket:eventbus:${versions.eventbus}"
    fun gson() = "com.google.code.gson:gson:${versions.gson}"
    fun sqlite_jdbc() = "org.xerial:sqlite-jdbc:${versions.sqlite_jdbc}"
    fun postgres_jdbc() = "org.postgresql:postgresql:${versions.postgres_jdbc}"
    fun mockk() = "io.mockk:mockk:${versions.mockk}"

    fun jpersistence() = "org.daiv.jpersistence:jpersistence:${versions.jpersistence}"
    fun coroutines() = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.coroutines}"
    fun kotlinx_html() = kotlinx("html", versions.kotlinx_html)
    fun ktor(module: String) = "io.ktor:ktor-$module:${versions.ktor}"
    fun kotlinx(module: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$module:$version"
}

class DefaultDependencyBuilder(override val versions: Versions = Versions.current()) : StandardBuilder


fun main() {
    System.setProperty("logback.configurationFile", "logback.xml")
//    val versions = Versions.versions()
//    val res = versions.copy(eventbus = versions.increment { eventbus })
//    println("v: $versions, $res")
//    println("v: $res")
}
