package org.daiv.dependency


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
)  {

    companion object {
        val versions1_4_0 = Versions(
            serialization = "1.0.0-RC",
            kutil = "0.3.2",
            coroutines = "1.3.9",
            ktor = "1.4.0",
            jpersistence = "0.9.1",
            eventbus = "0.5.2",
            gson = "2.8.5",
            kotlinx_html = "0.7.2",
            mockk = "1.9.2",
            sqlite_jdbc = "3.27.2.1",
            postgres_jdbc = "42.2.18"
        )
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
    fun ktor(module: String) = "io.ktor:ktor-$module:${versions.ktor}"
    fun kotlinx(module: String, version:String) = "org.jetbrains.kotlinx:kotlinx-$module:$version"
    fun kotlinx_html() = kotlinx("html", versions.kotlinx_html)
}
