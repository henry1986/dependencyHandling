package org.daiv.dependency

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

interface Depeable<KOTLINDEPENDENCYBUILDER : KotlinDependencyBuilder<KOTLINDEPENDENCYBUILDER>,
        GRADLEDEPENDENCYBUILDER : GradleDependencyBuilder<GRADLEDEPENDENCYBUILDER>> {
    fun builder(kotlinDependencyHandler: KotlinDependencyHandler): KOTLINDEPENDENCYBUILDER
    fun builder(dependencyHandler: DependencyHandler): GRADLEDEPENDENCYBUILDER
    fun deps(sourceSet: KotlinSourceSet, f: KOTLINDEPENDENCYBUILDER.() -> Unit) = sourceSet.dependencies {
        val builder = builder(this)
        builder.f()
    }

    fun dependencies(dependencyHandler: DependencyHandlerScope, f: GRADLEDEPENDENCYBUILDER.() -> Unit) {
        builder(dependencyHandler).f()
    }
}

interface DependencyBuilder {
    fun impl(value: Any)
}

interface GradleDependencyBuilder<T : GradleDependencyBuilder<T>> : DependencyBuilder {
    val dependencyHandler: DependencyHandler
    val buildName: String
    fun reset(buildName: String): GradleDependencyBuilder<T>
    override fun impl(value: Any) {
        dependencyHandler.add(buildName, value)
    }

    fun toApi() = reset("api")
    fun toImplementation() = reset("implementation")
}

interface KotlinDependencyBuilder<T : KotlinDependencyBuilder<T>> : DependencyBuilder {
    val kotlinDependencyHandler: KotlinDependencyHandler
    val dependencyBuilder: InternalDependencyBuilder

    fun reset(dependencyBuilder: InternalDependencyBuilder): KotlinDependencyBuilder<T>

    interface InternalDependencyBuilder {

        fun build(kotlinDependencyHandler: KotlinDependencyHandler, value: Any)


        fun buildKotlin(kotlinDependencyHandler: KotlinDependencyHandler, value: String)
    }

    object ImplementationBuilder : InternalDependencyBuilder {
        override fun build(kotlinDependencyHandler: KotlinDependencyHandler, value: Any) {
            kotlinDependencyHandler.implementation(value)
        }

        override fun buildKotlin(kotlinDependencyHandler: KotlinDependencyHandler, value: String) {
            kotlinDependencyHandler.implementation(kotlinDependencyHandler.kotlin(value))
        }
    }

    object ApiBuilder : InternalDependencyBuilder {
        override fun build(kotlinDependencyHandler: KotlinDependencyHandler, value: Any) {
            kotlinDependencyHandler.api(value)
        }

        override fun buildKotlin(kotlinDependencyHandler: KotlinDependencyHandler, value: String) {
            kotlinDependencyHandler.api(kotlinDependencyHandler.kotlin(value))
        }
    }

    override fun impl(value: Any) {
        dependencyBuilder.build(kotlinDependencyHandler, value)
    }

    fun kotlinImpl(value: String) {
        dependencyBuilder.buildKotlin(kotlinDependencyHandler, value)
    }

    fun kotlinModule(module: String) = kotlinImpl(module)

    fun toApi() = reset(dependencyBuilder = ApiBuilder)
    fun toImplementation() = reset(dependencyBuilder = ImplementationBuilder)

    fun implementation(value: Any) = ImplementationBuilder.build(kotlinDependencyHandler, value)
    fun api(string: String) = ApiBuilder.build(kotlinDependencyHandler, string)
}
