package com.synebula.gaea.app.autoconfig.service

import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited


@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@Import(ServiceRegister::class)
annotation class ServiceScan(val basePackages: Array<String> = [])