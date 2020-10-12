package org.daiv.dependency

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

interface Depeable<DEPENDENCYBUILDER : DependencyBuilder<DEPENDENCYBUILDER>> {
    fun builder(kotlinDependencyHandler: KotlinDependencyHandler): DEPENDENCYBUILDER
    fun deps(sourceSet: KotlinSourceSet, f: DEPENDENCYBUILDER.() -> Unit) = sourceSet.dependencies {
        val builder = builder(this)
        builder.f()
    }
}

data class Versions(
    val serialization: String,
    val kutil: String,
    val coroutines: String,
    val ktor: String,
    val jpersistence: String,
    val eventbus: String,
    val gson: String,
    val mockk: String,
    val sqlite_jdbc: String
) : Depeable<DefaultBuilder> {

    override fun builder(kotlinDependencyHandler: KotlinDependencyHandler) =
        DefaultBuilder(kotlinDependencyHandler, this)

    companion object {
        val versions1_4_0 = Versions(
            serialization = "1.0.0-RC",
            kutil = "0.3.0",
            coroutines = "1.3.9",
            ktor = "1.4.0",
            jpersistence = "0.9.1",
            eventbus = "0.5.2",
            gson = "2.8.5",
            mockk = "1.9.2",
            sqlite_jdbc = "3.27.2.1"
        )
    }
}

interface DependencyBuilder<T : DependencyBuilder<T>> {
    val kotlinDependencyHandler: KotlinDependencyHandler
    val dependencyBuilder: InternalDependencyBuilder

    fun reset(dependencyBuilder: InternalDependencyBuilder): DependencyBuilder<T>

    interface InternalDependencyBuilder {
        fun build(kotlinDependencyHandler: KotlinDependencyHandler, value: String)
        fun buildKotlin(kotlinDependencyHandler: KotlinDependencyHandler, value: String)
    }

    object ImplementationBuilder : InternalDependencyBuilder {
        override fun build(kotlinDependencyHandler: KotlinDependencyHandler, value: String) {
            kotlinDependencyHandler.implementation(value)
        }

        override fun buildKotlin(kotlinDependencyHandler: KotlinDependencyHandler, value: String) {
            kotlinDependencyHandler.implementation(kotlinDependencyHandler.kotlin(value))
        }
    }

    object ApiBuilder : InternalDependencyBuilder {
        override fun build(kotlinDependencyHandler: KotlinDependencyHandler, value: String) {
            kotlinDependencyHandler.api(value)
        }

        override fun buildKotlin(kotlinDependencyHandler: KotlinDependencyHandler, value: String) {
            kotlinDependencyHandler.api(kotlinDependencyHandler.kotlin(value))
        }
    }

    fun impl(value: String) {
        dependencyBuilder.build(kotlinDependencyHandler, value)
    }

    fun kotlinImpl(value: String) {
        dependencyBuilder.buildKotlin(kotlinDependencyHandler, value)
    }

    fun toApi() = reset(dependencyBuilder = ApiBuilder)
    fun toImplementation() = reset(dependencyBuilder = ImplementationBuilder)

    fun implementation(string: String) = ImplementationBuilder.build(kotlinDependencyHandler, string)
    fun api(string: String) = ApiBuilder.build(kotlinDependencyHandler, string)
}

interface StandardBuilder<T : StandardBuilder<T>> : DependencyBuilder<T> {
    val versions: Versions
    fun serialization() = impl("org.jetbrains.kotlinx:kotlinx-serialization-core:${versions.serialization}")
    fun kutil() = impl("org.daiv.util:kutil:${versions.kutil}")

    fun eventbus() = impl("org.daiv.websocket:eventbus:${versions.eventbus}")
    fun gson() = impl("com.google.code.gson:gson:${versions.gson}")
    fun sqlite_jdbc() = impl("org.xerial:sqlite-jdbc:${versions.sqlite_jdbc}")
    fun mockk() = impl("io.mockk:mockk:${versions.mockk}")

    fun jpersistence() = impl("org.daiv.jpersistence:jpersistence:${versions.jpersistence}")
    fun coroutines() = impl("org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.coroutines}")
    fun ktor(module: String) = impl("io.ktor:ktor-$module:${versions.ktor}")
    fun kotlinModule(module: String) = kotlinImpl(module)
}

data class DefaultBuilder internal constructor(
    override val kotlinDependencyHandler: KotlinDependencyHandler,
    override val versions: Versions,
    override val dependencyBuilder: DependencyBuilder.InternalDependencyBuilder = DependencyBuilder.ImplementationBuilder
) : StandardBuilder<DefaultBuilder> {
    override fun reset(dependencyBuilder: DependencyBuilder.InternalDependencyBuilder) =
        copy(dependencyBuilder = dependencyBuilder)
}

class DependencyHandling : Plugin<Project> {
    override fun apply(target: Project) {
    }
}
