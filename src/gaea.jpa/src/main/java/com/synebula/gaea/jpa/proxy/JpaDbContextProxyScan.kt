package com.synebula.gaea.jpa.proxy

import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@Import(JpaDbContextRegister::class)
annotation class JpaDbContextProxyScan(val basePackages: Array<String> = [], val scanInterfaces: Array<KClass<*>> = [])