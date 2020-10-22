package org.daiv.dependency

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

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
) : Depeable<DefaultKotlinDependencyBuilder, DefaultGradleDependencyBuilder> {

    override fun builder(kotlinDependencyHandler: KotlinDependencyHandler) =
        DefaultKotlinDependencyBuilder(kotlinDependencyHandler, this)

    override fun builder(dependencyHandler: DependencyHandler) = DefaultGradleDependencyBuilder(dependencyHandler, this)

    companion object {
        val versions1_4_0 = Versions(
            serialization = "1.0.0-RC",
            kutil = "0.3.0",
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

interface StandardBuilder : DependencyBuilder {
    val versions: Versions
    fun serialization() = impl("org.jetbrains.kotlinx:kotlinx-serialization-core:${versions.serialization}")
    fun kutil() = impl("org.daiv.util:kutil:${versions.kutil}")

    fun eventbus() = impl("org.daiv.websocket:eventbus:${versions.eventbus}")
    fun gson() = impl("com.google.code.gson:gson:${versions.gson}")
    fun sqlite_jdbc() = impl("org.xerial:sqlite-jdbc:${versions.sqlite_jdbc}")
    fun postgres_jdbc() = impl("org.postgresql:postgresql:${versions.postgres_jdbc}")
    fun mockk() = impl("io.mockk:mockk:${versions.mockk}")

    fun jpersistence() = impl("org.daiv.jpersistence:jpersistence:${versions.jpersistence}")
    fun coroutines() = impl("org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.coroutines}")
    fun ktor(module: String) = impl("io.ktor:ktor-$module:${versions.ktor}")
    fun kotlinx(module: String, version:String) = impl("org.jetbrains.kotlinx:kotlinx-$module:$version")
    fun kotlinx_html() = kotlinx("html", versions.kotlinx_html)
}

data class DefaultKotlinDependencyBuilder internal constructor(
    override val kotlinDependencyHandler: KotlinDependencyHandler,
    override val versions: Versions,
    override val dependencyBuilder: KotlinDependencyBuilder.InternalDependencyBuilder = KotlinDependencyBuilder.ImplementationBuilder
) : KotlinDependencyBuilder<DefaultKotlinDependencyBuilder>, StandardBuilder {
    override fun reset(dependencyBuilder: KotlinDependencyBuilder.InternalDependencyBuilder) =
        copy(dependencyBuilder = dependencyBuilder)
}

data class DefaultGradleDependencyBuilder internal constructor(
    override val dependencyHandler: DependencyHandler,
    override val versions: Versions,
    override val buildName: String = "implementation"
) :
    GradleDependencyBuilder<DefaultGradleDependencyBuilder>, StandardBuilder {
    override fun reset(buildName: String): GradleDependencyBuilder<DefaultGradleDependencyBuilder> {
        return copy(buildName = buildName)
    }
}